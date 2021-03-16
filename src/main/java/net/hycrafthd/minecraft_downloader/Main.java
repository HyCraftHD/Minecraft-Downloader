package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class Main {
	
	public static final Logger LOGGER = LogManager.getLogger("Minecraft Downloader");
	
	public static void main(String[] args) throws IOException {
		final OptionParser parser = new OptionParser();
		
		// Default specs
		final OptionSpec<Void> helpSpec = parser.accepts("help", "Show the help menu").forHelp();
		final OptionSpec<String> versionSpec = parser.accepts("version", "Minecraft version to download").withRequiredArg();
		final OptionSpec<File> outputSpec = parser.accepts("output", "Output folder").withRequiredArg().ofType(File.class);
		
		// Launch specs
		final OptionSpec<Void> launchSpec = parser.accepts("launch", "Launch minecraft after downloading the files");
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
		final String username = set.valueOf(usernameSpec);
		final String password = set.valueOf(passwordSpec);
		
		final boolean customResolution = set.has(widthSpec) || set.has(heightSpec);
		final int width = set.valueOf(widthSpec);
		final int height = set.valueOf(heightSpec);
		
		// Create output folder
		if (output.exists()) {
			if (!output.canWrite()) {
				LOGGER.fatal("Cannot write to the output folder");
				return;
			}
		} else {
			LOGGER.debug("Created output folder " + output.getAbsolutePath());
			output.mkdirs();
		}
		
		final CurrentClientJson client = MinecraftParser.launch(version, output);
		final List<LibraryParser> parsedLibraries = MinecraftDownloader.launch(client, output);
		if (launch) {
			MinecraftLauncher.launch(client, parsedLibraries, output);
		}
	}
	
}
