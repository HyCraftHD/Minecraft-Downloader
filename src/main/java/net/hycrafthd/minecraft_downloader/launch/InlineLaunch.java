package net.hycrafthd.minecraft_downloader.launch;

import java.lang.reflect.Method;

import net.hycrafthd.minecraft_downloader.Main;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class InlineLaunch {
	
	public static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Prepare inline launch");
		
		final ArgumentsParser parser = new ArgumentsParser(settings);
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		// Set properties for inline launch
		parser.getJvmArgs().stream().filter(arg -> arg.startsWith("-D")).forEach(propertyArgument -> {
			final String[] array = propertyArgument.split("=", 2);
			
			if (array.length != 2) {
				return;
			}
			
			final String property = array[0].substring(2);
			final String value = array[1];
			
			// Do not change library path (see comment below)
			if (property.equals("java.library.path")) {
				return;
			}
			
			System.setProperty(property, value);
		});
		
		// Set library paths. Should replace java.library.path as this path cannot be set after startup without very hacky
		// reflections
		System.setProperty("jna.library.path", settings.getNativesDirectory().getAbsolutePath());
		System.setProperty("org.lwjgl.librarypath", settings.getNativesDirectory().getAbsolutePath());
		
		// Does not seem to work (TODO fix config file reload in minecraft)
		System.setProperty("log4j.configurationFile", generatedSettings.getLogFile().getAbsolutePath());
		
		Main.LOGGER.info("Launch minecraft with inline launch");
		
		try {
			final Class<?> mainClass = Class.forName(generatedSettings.getClientJson().getMainClass(), true, generatedSettings.getClassLoader());
			
			final Method entryPoint = mainClass.getMethod("main", String[].class);
			
			entryPoint.invoke(null, new Object[] { parser.getGameArgs().stream().toArray(String[]::new) });
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to run minecraft", ex);
		}
	}
	
}
