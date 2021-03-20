package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import net.hycrafthd.minecraft_downloader.launch.ArgumentsParser;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftLauncher {
	
	static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start minecraft");
		
		setVariables(settings);
		launchInline(settings);
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
	
	private static void launchInline(ProvidedSettings settings) {
		Main.LOGGER.info("Start inline launch");
		
		final ArgumentsParser parser = new ArgumentsParser(settings);
		
		// Set properties for inline launch
		parser.getJvmArgs().stream().filter(arg -> arg.startsWith("-D")).forEach(propertyArgument -> {
			final String[] array = propertyArgument.split("=", 2);
			
			if (array.length != 2) {
				return;
			}
			
			final String property = array[0].substring(2);
			final String value = array[1];
			
			System.setProperty(property, value);
		});
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		try {
			final Class<?> mainClass = Class.forName(generatedSettings.getClientJson().getMainClass(), true, generatedSettings.getClassLoader());
			final Method entryPoint = mainClass.getMethod("main", String[].class);
			entryPoint.invoke(null, new Object[] { parser.getGameArgs() });
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to run minecraft", ex);
		}
	}
}
