package net.hycrafthd.minecraft_downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.UUID;

import net.hycrafthd.logging_util.LoggingUtil;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.login.file.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftAuthenticator {
	
	public static void launch(ProvidedSettings settings, File authFile, boolean authenticate, String authenticateType) {
		Main.LOGGER.info("Start the authenticator to log into minecraft");
		
		try {
			final AuthenticationFile startAuthFile;
			
			if (authenticate) {
				if (authenticateType == null || authenticateType.isEmpty() || authenticateType.equals("console")) {
					startAuthFile = consoleAuthentication();
				} else {
					throw new IllegalArgumentException("Authentication type is not known. Only 'console' is supported");
				}
			} else {
				try (final FileInputStream inputStream = new FileInputStream(authFile)) {
					startAuthFile = AuthenticationFile.read(inputStream);
				}
			}
			
			final Authenticator authenticator = Authenticator.of(startAuthFile) //
					.shouldAuthenticate() //
					.serviceConnectTimeout(10000) //
					.serviceReadTimeout(10000) //
					.run();
			
			final AuthenticationFile updatedAuthFile = authenticator.getResultFile();
			if (!startAuthFile.equals(updatedAuthFile)) {
				try (final FileOutputStream outputStream = new FileOutputStream(authFile)) {
					updatedAuthFile.write(outputStream);
				}
			}
			
			final User user = authenticator.getUser().get();
			
			LoggingUtil.addRemoveFromLog(user.getAccessToken());
			
			settings.addVariable(LauncherVariables.AUTH_PLAYER_NAME, user.getName());
			settings.addVariable(LauncherVariables.AUTH_UUID, user.getUuid());
			settings.addVariable(LauncherVariables.AUTH_ACCESS_TOKEN, user.getAccessToken());
			settings.addVariable(LauncherVariables.USER_TYPE, user.getType());
			
			Main.LOGGER.info("Logged into minecraft account");
		} catch (AuthenticationException | IOException | NoSuchElementException ex) {
			Main.LOGGER.info("An error occured while trying to log into minecraft account", ex);
		}
	}
	
	private static AuthenticationFile consoleAuthentication() throws IOException, AuthenticationException {
		Main.LOGGER.info("Authentication in console");
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				System.out.println("Type 'microsoft' or 'mojang' for account type");
				final String type = reader.readLine();
				
				if (type.equals("microsoft")) {
					System.out.println("Open the following link and log into your microsoft account");
					System.out.println(MicrosoftService.oAuthLoginUrl());
					System.out.println("Paste the code parameter of the returned url");
					final String authCode = reader.readLine();
					
					return Authenticator.ofMicrosoft(authCode).serviceConnectTimeout(10000).serviceReadTimeout(10000).run().getResultFile();
				} else if (type.equals("mojang")) {
					System.out.println("Type in your username / email");
					final String username = reader.readLine();
					System.out.println("Type in your password");
					final String password = reader.readLine();
					
					return Authenticator.ofYggdrasil(UUID.randomUUID().toString(), username, password).serviceConnectTimeout(10000).serviceReadTimeout(10000).run().getResultFile();
				}
			}
		}
	}
}
