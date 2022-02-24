package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.simple_minecraft_authenticator.SimpleMinecraftAuthentication;
import net.hycrafthd.simple_minecraft_authenticator.creator.AuthenticationMethodCreator;

public class MinecraftAuthenticator {
	
	public static void launch(ProvidedSettings settings, File authFile, boolean authenticate, String authenticateType) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		try {
			final Authenticator authenticator;
			
			if (authenticate) {
				final Optional<AuthenticationMethodCreator> method = SimpleMinecraftAuthentication.getMethod(authenticateType);
				if (method.isEmpty()) {
					throw new IllegalArgumentException("Authentication type " + authenticateType + " does not exist");
				}
				
				authenticator = method.get().create().initalAuthentication().buildAuthenticator();
			} else {
				try (final FileInputStream inputStream = new FileInputStream(authFile)) {
					final AuthenticationFile existingAuthFile = AuthenticationFile.readCompressed(inputStream);
					
					authenticator = SimpleMinecraftAuthentication.getDefaultMethod().create().existingAuthentication(existingAuthFile).buildAuthenticator();
				}
			}
			
			authenticator.run();
			
			try (final FileOutputStream outputStream = new FileOutputStream(authFile)) {
				authenticator.getResultFile().writeCompressed(outputStream);
			}
			
			final User user = authenticator.getUser().get();
			
			// Set base login information
			settings.addVariable(LauncherVariables.AUTH_PLAYER_NAME, user.name());
			settings.addVariable(LauncherVariables.AUTH_UUID, user.uuid());
			settings.addVariable(LauncherVariables.AUTH_ACCESS_TOKEN, user.accessToken());
			settings.addVariable(LauncherVariables.USER_TYPE, user.type());
			settings.addVariable(LauncherVariables.AUTH_XUID, user.xuid());
			settings.addVariable(LauncherVariables.CLIENT_ID, user.clientId());
			
			// Set legacy auth session
			settings.addVariable(LauncherVariables.AUTH_SESSION, "token:" + user.accessToken() + ":" + user.uuid());
			
			Main.LOGGER.info("Logged into minecraft account");
		} catch (AuthenticationException | IOException | NoSuchElementException ex) {
			Main.LOGGER.info("An error occured while trying to log into minecraft account", ex);
		}
	}
}
