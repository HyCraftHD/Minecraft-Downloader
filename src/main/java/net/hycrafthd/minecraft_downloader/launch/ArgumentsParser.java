package net.hycrafthd.minecraft_downloader.launch;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	public ArgumentsParser(ProvidedSettings settings) {
		final ArgumentsJson argumentsJson = settings.getGeneratedSettings().getClientJson().getArguments();
		
		gameArgs = replaceVariables(Stream.concat(argumentsJson.getGameArguments().stream(), conditionalGameArg(argumentsJson.getConditionalGameArguments().stream(), settings)), settings).collect(Collectors.toList());
		jvmArgs = replaceVariables(Stream.concat(conditionalJvmArg(argumentsJson.getConditionalJvmArguments().stream(), settings), argumentsJson.getJvmArguments().stream()), settings).collect(Collectors.toList());
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
					if (arch.equals(OSUtil.CURRENT_ARCH)) {
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
