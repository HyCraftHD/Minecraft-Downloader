package net.hycrafthd.minecraft_downloader.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalGameArgumentJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalJvmArgumentJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.BaseOsRuleJson.OSJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.LauncherFeatures;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.StringUtil;

public class ArgumentsParser {
	
	private final List<String> gameArgs;
	private final List<String> jvmArgs;
	
	public ArgumentsParser(ProvidedSettings settings, String standardJvmArgs) {
		this.gameArgs = new ArrayList<>();
		this.jvmArgs = new ArrayList<>();
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		final CurrentClientJson clientJson = generatedSettings.getClientJson();
		
		final ArgumentsJson argumentsJson = clientJson.getArguments();
		final String minecraftArguments = clientJson.getMinecraftArguments();
		
		if (argumentsJson != null) {
			buildArguments(settings, argumentsJson);
		} else if (minecraftArguments != null) {
			buildLegacyArguments(settings, minecraftArguments);
		} else {
			throw new IllegalStateException("Client json does not contains arguments on how to launch the game");
		}
		
		// Add standard jvm args
		Stream.of(standardJvmArgs.split(" ")).forEach(jvmArgs::add);
		
		// Add log file argument
		if (generatedSettings.getLogFile() != null && clientJson.getLogging() != null) {
			jvmArgs.add(StringUtil.replaceVariable("path", clientJson.getLogging().getClient().getArgument(), generatedSettings.getLogFile().getAbsolutePath()));
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
	
	private void buildArguments(ProvidedSettings settings, ArgumentsJson argumentsJson) {
		// Add game args
		replaceVariables(Stream.concat(argumentsJson.getGameArguments().stream(), conditionalGameArg(argumentsJson.getConditionalGameArguments().stream(), settings)), settings).forEach(gameArgs::add);
		
		// Add jvm args
		replaceVariables(Stream.concat(conditionalJvmArg(argumentsJson.getConditionalJvmArguments().stream(), settings), argumentsJson.getJvmArguments().stream()), settings).forEach(jvmArgs::add);
	}
	
	private void buildLegacyArguments(ProvidedSettings settings, String minecraftArguments) {
		// Add game args
		final List<String> preGameArgs = new ArrayList<String>();
		
		Stream.of(minecraftArguments.split(" ")).forEach(preGameArgs::add);
		if (settings.hasFeature(LauncherFeatures.DEMO_USER)) {
			gameArgs.add("--demo");
		}
		if (settings.hasFeature(LauncherFeatures.HAS_CUSTOM_RESOLUTION)) {
			gameArgs.add("--width");
			gameArgs.add("${resolution_width}");
			gameArgs.add("${height}");
			gameArgs.add("${resolution_height}");
		}
		
		replaceVariables(preGameArgs.stream(), settings).forEach(gameArgs::add);
		
		// Add jvm args
		final ArrayList<String> preJvmArgs = new ArrayList<>();
		
		preJvmArgs.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		preJvmArgs.add("-Dos.name=Windows 10");
		preJvmArgs.add("-Dos.version=10.0");
		preJvmArgs.add("-Djava.library.path=${natives_directory}");
		preJvmArgs.add("-Dminecraft.launcher.brand=${launcher_name}");
		preJvmArgs.add("-Dminecraft.launcher.version=${launcher_version}");
		preJvmArgs.add("-Dminecraft.client.jar=${primary_jar}");
		preJvmArgs.add("-cp");
		preJvmArgs.add("${classpath}");
		
		replaceVariables(preJvmArgs.stream(), settings).forEach(jvmArgs::add);
	}
	
	public List<String> getGameArgs() {
		return gameArgs;
	}
	
	public List<String> getJvmArgs() {
		return jvmArgs;
	}
	
}
