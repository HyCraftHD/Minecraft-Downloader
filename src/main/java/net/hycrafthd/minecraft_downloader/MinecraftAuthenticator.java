package net.hycrafthd.minecraft_downloader;

import net.hycrafthd.minecraft_downloader.auth.api.MinecraftAuth;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftAuthenticator {
	
	static void launch(ProvidedSettings settings, String username, String password) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		final ClassLoader classLoader = settings.getGeneratedSettings().getClassLoader();
		
		try {
			final Class<? extends MinecraftAuth> authImplClass = Class.forName("net.hycrafthd.minecraft_downloader.auth.MinecraftAuthImpl", true, classLoader).asSubclass(MinecraftAuth.class);
			final MinecraftAuth auth = authImplClass.getConstructor(String.class, String.class).newInstance(username, password);
			
			auth.logIn();
			
			System.out.println(auth.getAuthenticatedToken());
			System.out.println(auth.getUUID());
			System.out.println(auth.getName());
			System.out.println(auth.getUserType());
		} catch (Exception ex) {
			throw new IllegalStateException("An exception occured during authentication", ex);
		}
	}
}
