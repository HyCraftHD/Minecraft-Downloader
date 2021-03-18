package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class Main {
	
	public static final Logger LOGGER = LogManager.getLogger("Minecraft Downloader");
	
	public static void main(String[] args) throws IOException {
		final OptionParser parser = new OptionParser();
		
		// Default specs
		final OptionSpec<Void> helpSpec = parser.accepts("help", "Show the help menu").forHelp();
		final OptionSpec<String> versionSpec = parser.accepts("version", "Minecraft version to download").withRequiredArg();
		final OptionSpec<File> outputSpec = parser.accepts("output", "Output directory for the downloaded files").withRequiredArg().ofType(File.class);
		
		// Launch specs
		final OptionSpec<Void> launchSpec = parser.accepts("launch", "Launch minecraft after downloading the files");
		final OptionSpec<File> runSpec = parser.accepts("run", "Run directory for the game").requiredIf(launchSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<String> usernameSpec = parser.accepts("username", "Username / Email for login").requiredIf(launchSpec).withRequiredArg();
		final OptionSpec<String> passwordSpec = parser.accepts("password", "Password for login").requiredIf(launchSpec).withRequiredArg();
		
		final OptionSpec<Integer> widthSpec = parser.accepts("width", "Width of the window").withRequiredArg().ofType(Integer.class);
		final OptionSpec<Integer> heightSpec = parser.accepts("height", "Height of the window").withRequiredArg().ofType(Integer.class);
		
		final OptionSet set = parser.parse(args);
		
		if (set.has(helpSpec) || set.specs().size() < 2) {
			parser.printHelpOn(IoBuilder.forLogger(LOGGER).setAutoFlush(true).setLevel(Level.ERROR).buildPrintStream());
			return;
		}
		
		LOGGER.info("Starting Minecraft Downloader");
		
		// Get arguments
		final String version = set.valueOf(versionSpec);
		final File output = set.valueOf(outputSpec);
		
		final boolean launch = set.has(launchSpec);
		final File run = set.valueOf(runSpec);
		final String username = set.valueOf(usernameSpec);
		final String password = set.valueOf(passwordSpec);
		
		final boolean customResolution = set.has(widthSpec) || set.has(heightSpec);
		final int width = set.valueOf(widthSpec);
		final int height = set.valueOf(heightSpec);
		
		// Create output folder
		if (FileUtil.createFolders(output)) {
			LOGGER.debug("Created output folder " + output.getAbsolutePath());
		}
		
		// Create provided settings
		final ProvidedSettings settings = new ProvidedSettings(version, output, run);
		
		MinecraftParser.launch(settings);
		MinecraftDownloader.launch(settings);
		
		if (launch) {
			MinecraftLauncher.launch(settings);
		}
	}
}
