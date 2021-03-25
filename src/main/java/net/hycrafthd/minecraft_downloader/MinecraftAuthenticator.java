package net.hycrafthd.minecraft_downloader;

import net.hycrafthd.logging_util.LoggingUtil;
import net.hycrafthd.minecraft_downloader.auth.api.MinecraftAuth;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftAuthenticator {
	
	static void launch(ProvidedSettings settings, String username, String password) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		try {
			final ClassLoader classLoader = settings.getGeneratedSettings().getClassLoader();
			
			final Class<? extends MinecraftAuth> authImplClass = Class.forName("net.hycrafthd.minecraft_downloader.auth.MinecraftAuthImpl", true, classLoader).asSubclass(MinecraftAuth.class);
			final MinecraftAuth auth = authImplClass.getConstructor(String.class, String.class).newInstance(username, password);
			
			LoggingUtil.addRemoveFromLog(username);
			LoggingUtil.addRemoveFromLog(password);
			
			LoggingUtil.disableLogging();
			
			auth.logIn();
			
			LoggingUtil.enableLogging();
			
			LoggingUtil.addRemoveFromLog(auth.getAuthenticatedToken());
			
			settings.addVariable(LauncherVariables.AUTH_PLAYER_NAME, auth.getName());
			settings.addVariable(LauncherVariables.AUTH_UUID, auth.getUUID());
			settings.addVariable(LauncherVariables.AUTH_ACCESS_TOKEN, auth.getAuthenticatedToken());
			settings.addVariable(LauncherVariables.USER_TYPE, auth.getUserType());
		} catch (Exception ex) {
			throw new IllegalStateException("An exception occured during authentication", ex);
		}
		
		Main.LOGGER.info("Logged into minecraft account");
	}
}
