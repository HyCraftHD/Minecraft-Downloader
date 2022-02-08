package net.hycrafthd.minecraft_downloader.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.hycrafthd.minecraft_downloader.Constants;
import net.hycrafthd.minecraft_downloader.Main;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil.OS;

public class ProcessLaunch {
	
	// TODO make them accessible in the arguments
	private static final String STANDARD_JVM_ARGS = "-Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M";
	
	public static void launch(ProvidedSettings settings, File javaExec) {
		Main.LOGGER.info("Prepare process launch");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final ArgumentsParser parser = new ArgumentsParser(settings, STANDARD_JVM_ARGS);
		final String java = findJavaExecutable(javaExec);
		
		// Build command for process builder
		final List<String> commands = new ArrayList<>();
		commands.add(java);
		commands.addAll(parser.getJvmArgs());
		commands.add(generatedSettings.getClientJson().getMainClass());
		commands.addAll(parser.getGameArgs());
		
		// Process builder
		final ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.directory(settings.getRunDirectory());
		processBuilder.inheritIO();
		
		Main.LOGGER.debug("Process starts with commands:");
		processBuilder.command().forEach(command -> {
			Main.LOGGER.debug(" " + command);
		});
		
		Main.LOGGER.info("Launch minecraft as a new process");
		try {
			final Process process = processBuilder.start();
			process.waitFor();
		} catch (final IOException | InterruptedException ex) {
			throw new IllegalStateException("Failed to run minecraft", ex);
		}
		Main.LOGGER.info("Closing minecraft downloader");
	}
	
	private static String findJavaExecutable(File javaExec) {
		if (javaExec == null || !javaExec.canRead()) {
			final String javaFile;
			if (OSUtil.CURRENT_OS == OS.WINDOWS) {
				javaFile = "java.exe";
			} else {
				javaFile = "java";
			}
			
			return new File(System.getProperty("java.home"), "bin" + Constants.FILE_SEPERATOR + javaFile).getAbsolutePath();
		} else {
			return javaExec.getAbsolutePath();
		}
	}
	
}
