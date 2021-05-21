package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;

import net.hycrafthd.logging_util.LoggingUtil;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftAuthenticator {
	
	public static void launch(ProvidedSettings settings, File authFile, boolean authenticate, String authenticateType) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		if (authenticate) {
			if (authenticateType == null || authenticateType.isEmpty() || authenticateType.equals("console")) {
				consoleAuthentication(authFile);
			} else {
				throw new IllegalArgumentException("Authentication type is not known. Only console is supported right now");
			}
		}
		
		try {
			final AuthenticationFile startAuthFile = AuthenticationFile.read(authFile.toPath());
			final Authenticator authenticator = Authenticator.of(startAuthFile).shouldAuthenticate().run();
			
			final AuthenticationFile updatedAuthFile = authenticator.getResultFile();
			if (!startAuthFile.equals(updatedAuthFile)) {
				updatedAuthFile.write(authFile.toPath());
			}
			
			final User user = authenticator.getUser().get();
			
			LoggingUtil.addRemoveFromLog(user.getAccessToken());
			
			settings.addVariable(LauncherVariables.AUTH_PLAYER_NAME, user.getName());
			settings.addVariable(LauncherVariables.AUTH_UUID, user.getUuid());
			settings.addVariable(LauncherVariables.AUTH_ACCESS_TOKEN, user.getAccessToken());
			settings.addVariable(LauncherVariables.USER_TYPE, user.getType());
			
			Main.LOGGER.info("Logged into minecraft account");
		} catch (AuthenticationException | IOException ex) {
			Main.LOGGER.info("An error occured while trying to log into minecraft account", ex);
		}
	}
	
	private static void consoleAuthentication(File authFile) {
		Main.LOGGER.info("Authentication in console");
		try {
			// TODO Implement the console login in a better way than calling an others libraries main function
			net.hycrafthd.minecraft_authenticator.Main.main(new String[] { "--auth-file", authFile.toPath().toString() });
		} catch (Exception ex) {
			Main.LOGGER.info("An error occured while authentication", ex);
		}
	}
}
