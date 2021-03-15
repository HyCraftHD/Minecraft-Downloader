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
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class Main {
	
	public static final Logger LOGGER = LogManager.getLogger("Minecraft Downloader");
	
	public static void main(String[] args) throws IOException {
		final OptionParser parser = new OptionParser();
		
		final OptionSpec<Void> helpSpec = parser.accepts("help", "Show the help menu").forHelp();
		final OptionSpec<String> versionSpec = parser.accepts("version", "Minecraft version to download").withRequiredArg();
		final OptionSpec<File> outputSpec = parser.accepts("output", "Output folder").withRequiredArg().ofType(File.class);
		
		final OptionSet set = parser.parse(args);
		
		if (set.has(helpSpec) || set.specs().size() < 2) {
			parser.printHelpOn(IoBuilder.forLogger(LOGGER).setAutoFlush(true).setLevel(Level.INFO).buildPrintStream());
			return;
		}
		
		LOGGER.info("Starting Minecraft Downloader");
		
		final String version = set.valueOf(versionSpec);
		final File output = set.valueOf(outputSpec);
		
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
		MinecraftDownloader.launch(client, output);
	}
	
}
