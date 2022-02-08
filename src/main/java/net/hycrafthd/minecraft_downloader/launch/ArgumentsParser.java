package net.hycrafthd.minecraft_downloader.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalGameArgumentJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalJvmArgumentJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.BaseOsRuleJson.OSJson;
import net.hycrafthd.minecraft_downloader.settings.LauncherFeatures;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.OSUtil;

public class ArgumentsParser {
	
	private final List<String> gameArgs;
	private final List<String> jvmArgs;
	
	public ArgumentsParser(ProvidedSettings settings, String standardJvmArgs) {
		final CurrentClientJson clientJson = settings.getGeneratedSettings().getClientJson();
		
		final ArgumentsJson argumentsJson = clientJson.getArguments();
		final String minecraftArguments = clientJson.getMinecraftArguments();
		
		if (argumentsJson != null) {
			gameArgs = replaceVariables(Stream.concat(argumentsJson.getGameArguments().stream(), conditionalGameArg(argumentsJson.getConditionalGameArguments().stream(), settings)), settings).collect(Collectors.toList());
			jvmArgs = replaceVariables(Stream.concat(conditionalJvmArg(argumentsJson.getConditionalJvmArguments().stream(), settings), argumentsJson.getJvmArguments().stream()), settings).collect(Collectors.toList());
		} else if (minecraftArguments != null) {
			gameArgs = replaceVariables(Stream.of(minecraftArguments.replace("${user_properties}", "{}").split(" ")), settings).collect(Collectors.toList());
			
			final ArrayList<String> preJvmArgs = new ArrayList<>(); // TODO clean up, and make modular
			preJvmArgs.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
			preJvmArgs.add("-Dos.name=Windows 10");
			preJvmArgs.add("-Dos.version=10.0");
			preJvmArgs.add("-Djava.library.path=${natives_directory}");
			preJvmArgs.add("-Dminecraft.launcher.brand=${launcher_name}");
			preJvmArgs.add("-Dminecraft.launcher.version=${launcher_version}");
			preJvmArgs.add("-Dminecraft.client.jar=" + settings.getClientJarFile().getAbsolutePath());
			preJvmArgs.add("-cp");
			preJvmArgs.add("${classpath}");
			
			jvmArgs = replaceVariables(preJvmArgs.stream(), settings).collect(Collectors.toList());
		} else {
			throw new IllegalStateException("Client json does not contains arguments on how to launch the game.s");
		}
	}
	
	private final Stream<String> replaceVariables(Stream<String> arguments, ProvidedSettings settings) {
		return arguments.map(argument -> settings.replaceVariable(argument));
	}
	
	private final Stream<String> conditionalGameArg(Stream<ConditionalGameArgumentJson> arguments, ProvidedSettings settings) {
		return arguments.filter(argument -> {
			return argument.getRules().stream().allMatch(rule -> {
				final boolean value = rule.getAction().equals("allow");
				if (rule.getFeatures().isIsDemoUser() && settings.hasFeature(LauncherFeatures.DEMO_USER)) {
					return value;
				}
				if (rule.getFeatures().isHasCustomResolution() && settings.hasFeature(LauncherFeatures.HAS_CUSTOM_RESOLUTION)) {
					return value;
				}
				return !value;
			});
		}).flatMap(argument -> argument.getValue().getValue().stream());
	}
	
	private final Stream<String> conditionalJvmArg(Stream<ConditionalJvmArgumentJson> arguments, ProvidedSettings settings) {
		return arguments.filter(argument -> {
			return argument.getRules().stream().allMatch(rule -> {
				final boolean value = rule.getAction().equals("allow");
				
				final OSJson os = rule.getOs();
				
				final String name = os.getName();
				final String version = os.getVersion();
				final String arch = os.getArch();
				
				boolean returnValue = !value;
				
				if (name != null) {
					if (name.equals(OSUtil.CURRENT_OS.getName())) {
						returnValue = value;
					} else {
						returnValue = !value;
					}
				}
				if (version != null) {
					if (Pattern.compile(version).matcher(OSUtil.CURRENT_VERSION).find()) {
						returnValue = value;
					} else {
						returnValue = !value;
					}
				}
				if (arch != null) {
					if (Pattern.compile(arch).matcher(OSUtil.CURRENT_ARCH).find()) {
						returnValue = value;
					} else {
						returnValue = !value;
					}
				}
				
				return returnValue;
			});
		}).flatMap(argument -> argument.getValue().getValue().stream());
	}
	
	public List<String> getGameArgs() {
		return gameArgs;
	}
	
	public List<String> getJvmArgs() {
		return jvmArgs;
	}
	
}
