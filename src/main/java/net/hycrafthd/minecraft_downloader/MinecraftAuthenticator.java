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
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.simple_minecraft_authenticator.SimpleMinecraftAuthentication;
import net.hycrafthd.simple_minecraft_authenticator.creator.AuthenticationMethodCreator;
import net.hycrafthd.simple_minecraft_authenticator.util.SimpleAuthenticationFileUtil;
import net.hycrafthd.simple_minecraft_authenticator.util.SimpleAuthenticationFileUtil.AuthenticationData;

public class MinecraftAuthenticator {
	
	public static void launch(ProvidedSettings settings, File authFile, String authMethod, boolean headlessAuth) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		if (headlessAuth) {
			Main.LOGGER.error("Force headless authentication");
		}
		
		try (final PrintStream out = IoBuilder.forLogger(Main.LOGGER).setAutoFlush(true).setLevel(Level.INFO).buildPrintStream()) {
			User user = null;
			if (authFile.exists()) {
				user = useExistingAuthFile(authFile, headlessAuth, out);
			}
			if (user == null) {
				user = createNewAuthFile(authFile, authMethod, headlessAuth, out);
			}
			
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
		} catch (final AuthenticationException | IOException | NoSuchElementException | IllegalArgumentException ex) {
			Main.LOGGER.info("An error occured while trying to log into minecraft account", ex);
		}
	}
	
	private static User useExistingAuthFile(File authFile, boolean headlessAuth, PrintStream out) throws IOException {
		if (!FileUtil.checkFile(authFile)) {
			throw new IllegalArgumentException("Authentication file " + authFile.getAbsolutePath() + " could not be read");
		}
		
		final Authenticator authenticator;
		final AuthenticationMethodCreator creator;
		
		try (final FileInputStream inputStream = new FileInputStream(authFile)) {
			final AuthenticationData authenticationData = SimpleAuthenticationFileUtil.read(inputStream.readAllBytes());
			
			creator = authenticationData.creator();
			authenticator = creator.create(headlessAuth, out, System.in).existingAuthentication(authenticationData.file()).buildAuthenticator();
		} catch (final IOException ex) {
			Main.LOGGER.warn("Existing auth file is corrupted");
			Main.LOGGER.catching(Level.DEBUG, ex);
			return null;
		}
		
		try {
			authenticator.run();
		} catch (final AuthenticationException ex) {
			Main.LOGGER.warn("Could not authenticate with existing auth file");
			Main.LOGGER.catching(Level.DEBUG, ex);
			
			if (authenticator.getResultFile() != null) {
				saveAuthFile(authFile, authenticator.getResultFile(), creator);
			}
			return null;
		}
		
		saveAuthFile(authFile, authenticator.getResultFile(), creator);
		return authenticator.getUser().get();
	}
	
	private static User createNewAuthFile(File authFile, String authMethod, boolean headlessAuth, PrintStream out) throws AuthenticationException, IOException {
		final AuthenticationMethodCreator creator = SimpleMinecraftAuthentication.getMethodOrThrow(authMethod);
		final Authenticator authenticator = creator.create(headlessAuth, out, System.in).initalAuthentication().buildAuthenticator();
		
		try {
			authenticator.run();
		} catch (final AuthenticationException ex) {
			if (authenticator.getResultFile() != null) {
				saveAuthFile(authFile, authenticator.getResultFile(), creator);
			}
			throw ex;
		}
		
		saveAuthFile(authFile, authenticator.getResultFile(), creator);
		return authenticator.getUser().get();
	}
	
	private static void saveAuthFile(File authFile, AuthenticationFile resultFile, AuthenticationMethodCreator creator) throws IOException {
		final byte[] bytes = SimpleAuthenticationFileUtil.write(new AuthenticationData(resultFile, creator));
		try (final FileOutputStream outputStream = new FileOutputStream(authFile)) {
			outputStream.write(bytes);
		} catch (final IOException ex) {
			Main.LOGGER.error("Cannot write authentication result file to {}", authFile.getAbsolutePath());
			throw ex;
		}
	}
}
