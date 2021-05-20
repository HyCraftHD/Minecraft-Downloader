package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.util.stream.Collectors;

import net.hycrafthd.minecraft_downloader.launch.ProcessLaunch;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftLauncher {
	
	public static void launch(ProvidedSettings settings, File javaExec) {
		Main.LOGGER.info("Start minecraft");
		
		setVariables(settings);
		ProcessLaunch.launch(settings, javaExec);
	}
	
	private static void setVariables(ProvidedSettings settings) {
		Main.LOGGER.info("Set variables for start");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final CurrentClientJson client = generatedSettings.getClientJson();
		
		settings.addVariable(LauncherVariables.VERSION_NAME, client.getId());
		settings.addVariable(LauncherVariables.VERSION_TYPE, client.getType());
		
		settings.addVariable(LauncherVariables.GAME_DIRECTORY, settings.getRunDirectory());
		
		settings.addVariable(LauncherVariables.ASSET_ROOT, settings.getAssetsDirectory());
		settings.addVariable(LauncherVariables.ASSET_INDEX_NAME, client.getAssetIndex().getId());
		
		settings.addVariable(LauncherVariables.LAUNCHER_NAME, Constants.NAME);
		settings.addVariable(LauncherVariables.LAUNCHER_VERSION, Constants.VERSION);
		
		settings.addVariable(LauncherVariables.NATIVE_DIRECTORY, settings.getNativesDirectory());
		settings.addVariable(LauncherVariables.CLASSPATH, generatedSettings.getClassPath().stream().map(File::getAbsolutePath).collect(Collectors.joining(";")));
	}
}
