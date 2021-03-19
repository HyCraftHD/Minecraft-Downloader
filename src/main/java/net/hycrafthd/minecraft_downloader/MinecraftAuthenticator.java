package net.hycrafthd.minecraft_downloader;

import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftAuthenticator {
	
	static void launch(ProvidedSettings settings, String username, String password) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		final ClassLoader classLoader = settings.getGeneratedSettings().getClassLoader();
		
		System.out.println(classLoader);
		
	}
	
}
