package net.hycrafthd.minecraft_downloader.launch;

import java.util.List;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class ArgumentsParser {
	
	private final String[] gameArgs;
	
	public ArgumentsParser(ArgumentsJson arguments, ProvidedSettings provided) {
		gameArgs = parseArguments(arguments.getGameArguments(), provided);
	}
	
	private final String[] parseArguments(List<String> arguments, ProvidedSettings provided) {
		return arguments.stream().map(argument -> provided.replaceVariable(argument)).toArray(String[]::new);
	}
	
	public String[] getGameArgs() {
		return gameArgs;
	}
	
}
