package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.util.ConsumerWithIOException;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.simple_minecraft_authenticator.SimpleMinecraftAuthentication;
import net.hycrafthd.simple_minecraft_authenticator.creator.AuthenticationMethodCreator;
import net.hycrafthd.simple_minecraft_authenticator.util.SimpleAuthenticationFileUtil;
import net.hycrafthd.simple_minecraft_authenticator.util.SimpleAuthenticationFileUtil.AuthenticationData;

public class MinecraftAuthenticator {
	
	public static void launch(ProvidedSettings settings, File authFile, boolean authenticate, String authenticateMethod, boolean headlessAuthenticate) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		try (final PrintStream out = IoBuilder.forLogger(Main.LOGGER).setAutoFlush(true).setLevel(Level.INFO).buildPrintStream()) {
			final Authenticator authenticator;
			final AuthenticationMethodCreator creator;
			
			if (headlessAuthenticate) {
				Main.LOGGER.error("Force headless authentication");
			}
			
			if (authenticate) {
				creator = SimpleMinecraftAuthentication.getMethodOrThrow(authenticateMethod);
				authenticator = creator.create(headlessAuthenticate, out, System.in).initalAuthentication().buildAuthenticator();
			} else {
				try (final FileInputStream inputStream = new FileInputStream(authFile)) {
					final AuthenticationData authenticationData = SimpleAuthenticationFileUtil.read(inputStream.readAllBytes());
					creator = authenticationData.creator();
					
					authenticator = creator.create(headlessAuthenticate, out, System.in).existingAuthentication(authenticationData.file()).buildAuthenticator();
				}
			}
			
			final ConsumerWithIOException<AuthenticationFile> saveResultFile = resultFile -> {
				final byte[] bytes = SimpleAuthenticationFileUtil.write(new AuthenticationData(resultFile, creator));
				try (final FileOutputStream outputStream = new FileOutputStream(authFile)) {
					outputStream.write(bytes);
				}
			};
			
			try {
				authenticator.run();
			} catch (final AuthenticationException ex) {
				Main.LOGGER.error("An authentication error occured. Saving authentication result file");
				if (authenticator.getResultFile() != null) {
					saveResultFile.accept(authenticator.getResultFile());
				}
				throw ex;
			}
			
			saveResultFile.accept(authenticator.getResultFile());
			
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
		} catch (final AuthenticationException | IOException | NoSuchElementException ex) {
			Main.LOGGER.info("An error occured while trying to log into minecraft account", ex);
		}
	}
}
