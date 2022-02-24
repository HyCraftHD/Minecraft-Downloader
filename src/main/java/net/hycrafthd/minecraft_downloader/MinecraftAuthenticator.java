package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;

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
		
		try (final PrintStream out = IoBuilder.forLogger(Main.LOGGER).setAutoFlush(true).setLevel(Level.INFO).buildPrintStream()) {
			final Authenticator authenticator;
			final String authMethod;
			
			if (authenticate) {
				final Optional<AuthenticationMethodCreator> method = SimpleMinecraftAuthentication.getMethod(authenticateType);
				if (method.isEmpty()) {
					throw new IllegalArgumentException("Authentication type " + authenticateType + " does not exist");
				}
				
				final AuthenticationMethodCreator creator = method.get();
				
				authenticator = creator.create(out, System.in).initalAuthentication().buildAuthenticator();
				authMethod = creator.name();
			} else {
				try (final FileInputStream inputStream = new FileInputStream(authFile)) {
					final AuthenticationFile existingAuthFile = AuthenticationFile.readCompressed(inputStream);
					
					final String requiredMethod = existingAuthFile.getExtraProperties().get("method");
					
					final AuthenticationMethodCreator creator;
					if (requiredMethod == null || !SimpleMinecraftAuthentication.getAvailableMethods().contains(requiredMethod)) {
						creator = SimpleMinecraftAuthentication.getDefaultMethod();
					} else {
						creator = SimpleMinecraftAuthentication.getMethod(requiredMethod).get();
					}
					
					authenticator = creator.create(out, System.in).existingAuthentication(existingAuthFile).buildAuthenticator();
					authMethod = creator.name();
				}
			}
			
			authenticator.run();
			
			try (final FileOutputStream outputStream = new FileOutputStream(authFile)) {
				final AuthenticationFile resultFile = authenticator.getResultFile();
				resultFile.getExtraProperties().put("method", authMethod);
				resultFile.writeCompressed(outputStream);
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
