package net.hycrafthd.minecraft_downloader.launch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.io.IoBuilder;

import net.hycrafthd.minecraft_downloader.Main;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class ProcessLaunch {
	
	private static final Marker LAUNCH_MARKER = MarkerManager.getMarker("LAUNCH");
	
	public static void launch(ProvidedSettings settings, String standardJvmArguments) {
		Main.LOGGER.info("Prepare process launch");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final ArgumentsParser parser = new ArgumentsParser(settings, standardJvmArguments);
		final String java = generatedSettings.getJavaExec().getAbsolutePath();
		
		// Build command for process builder
		final List<String> commands = new ArrayList<>();
		commands.add(java);
		commands.addAll(parser.getJvmArgs());
		commands.add(generatedSettings.getClientJson().getMainClass());
		commands.addAll(parser.getGameArgs());
		
		// Process builder
		final ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.directory(settings.getRunDirectory());
		processBuilder.redirectErrorStream(true);
		
		Main.LOGGER.info("Java executable is {}", java);
		
		Main.LOGGER.debug("Process starts with commands:");
		processBuilder.command().forEach(command -> {
			Main.LOGGER.debug(" " + command);
		});
		
		Main.LOGGER.info("Launch minecraft as a new process");
		try {
			final Process process = processBuilder.start();
			
			final Thread ioThread = new Thread(() -> {
				try (final OutputStream outputStream = IoBuilder.forLogger(Main.LOGGER).setAutoFlush(true).setLevel(Level.INFO).setMarker(LAUNCH_MARKER).buildOutputStream()) {
					process.getInputStream().transferTo(outputStream);
				} catch (final IOException ex) {
					Main.LOGGER.error("Cannot print minecraft log", ex);
				}
			});
			ioThread.setName("Minecraft Logger");
			ioThread.start();
			
			final int exitCode = process.waitFor();
			
			Main.LOGGER.info("Minecraft closed with exit code {}", exitCode);
		} catch (final IOException | InterruptedException ex) {
			throw new IllegalStateException("Failed to run minecraft", ex);
		}
		Main.LOGGER.info("Closing minecraft downloader");
	}
	
}
