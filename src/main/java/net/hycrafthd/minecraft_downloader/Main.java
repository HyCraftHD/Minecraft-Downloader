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
import net.hycrafthd.logging_util.LoggingUtil;
import net.hycrafthd.minecraft_downloader.settings.LauncherFeatures;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class Main {
	
	public static final Logger LOGGER = LogManager.getLogger("Minecraft Downloader");
	
	public static void main(String[] args) throws IOException {
		LoggingUtil.redirectPrintStreams(LOGGER);
		
		final OptionParser parser = new OptionParser();
		
		// Default specs
		final OptionSpec<Void> helpSpec = parser.accepts("help", "Show the help menu").forHelp();
		final OptionSpec<String> versionSpec = parser.accepts("version", "Minecraft version to download").withRequiredArg();
		final OptionSpec<File> outputSpec = parser.accepts("output", "Output directory for the downloaded files").withRequiredArg().ofType(File.class);
		
		// Launch specs
		final OptionSpec<Void> launchSpec = parser.accepts("launch", "Launch minecraft after downloading the files");
		final OptionSpec<File> javaExecSpec = parser.accepts("java-exec", "Which java executable should be used to launch minecraft. If non java executable is set, the jre from this process will be used").availableIf(launchSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<File> runSpec = parser.accepts("run", "Run directory for the game").availableIf(launchSpec).requiredIf(launchSpec).withRequiredArg().ofType(File.class);
		
		final OptionSpec<Void> defaultLogSpec = parser.accepts("default-log-config", "Use vanilla supplied log4j configuration").availableIf(launchSpec);
		final OptionSpec<File> logFileSpec = parser.accepts("log-config", "Use the specified file as log4j configuration").availableIf(launchSpec).availableUnless(defaultLogSpec).withRequiredArg().ofType(File.class);
		
		final OptionSpec<Void> demoSpec = parser.accepts("demo", "Start the demo mode").availableIf(launchSpec);
		
		final OptionSpec<Integer> widthSpec = parser.accepts("width", "Width of the window").availableIf(launchSpec).withRequiredArg().ofType(Integer.class);
		final OptionSpec<Integer> heightSpec = parser.accepts("height", "Height of the window").availableIf(launchSpec).withRequiredArg().ofType(Integer.class);
		
		// Login specs
		final OptionSpec<File> authFileSpec = parser.accepts("auth-file", "Authentication file for reading, writing and updating authentication data").withRequiredArg().ofType(File.class);
		final OptionSpec<String> authenticateSpec = parser.accepts("authenticate", "Lets the user login a mojang or microsoft accounts to create an authentication file. Currently console is supported").availableIf(authFileSpec).withRequiredArg().defaultsTo("console");
		
		// Special specs
		final OptionSpec<Void> skipNativesSpec = parser.accepts("skip-natives", "Skip extracting natives").availableUnless(launchSpec);
		final OptionSpec<Void> skipAssetsSpec = parser.accepts("skip-assets", "Skip the assets downloader").availableUnless(launchSpec);
		
		// Information specs
		final OptionSpec<Void> informationSpec = parser.accepts("extra-information", "Should extra information be extracted");
		final OptionSpec<File> userDataSpec = parser.accepts("user-data", "Create a file with the user login information").availableIf(informationSpec).availableIf(authFileSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<File> libraryListSpec = parser.accepts("library-list", "Create a library list file with all library excluding natives").availableIf(informationSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<File> libraryListNativesSpec = parser.accepts("library-list-natives", "Create a library list file with only native libraries").availableIf(informationSpec).withRequiredArg().ofType(File.class);
		
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
		final File javaExec = set.valueOf(javaExecSpec);
		final File run = set.valueOf(runSpec);
		
		final boolean defaultLog = set.has(defaultLogSpec);
		final File logFile = set.valueOf(logFileSpec);
		
		final boolean demo = set.has(demoSpec);
		
		final boolean customResolution = set.has(widthSpec) && set.has(heightSpec);
		final Integer width = set.valueOf(widthSpec);
		final Integer height = set.valueOf(heightSpec);
		
		final File authFile = set.valueOf(authFileSpec);
		final boolean authenticate = set.has(authenticateSpec);
		final String authenticateType = set.valueOf(authenticateSpec);
		
		final boolean skipNatives = set.has(skipNativesSpec);
		final boolean skipAssets = set.has(skipAssetsSpec);
		
		final boolean information = set.has(informationSpec);
		final File userData = set.valueOf(userDataSpec);
		final File libraryList = set.valueOf(libraryListSpec);
		final File libraryListNatives = set.valueOf(libraryListNativesSpec);
		
		// Create output folder
		if (FileUtil.createFolders(output)) {
			LOGGER.debug("Created output folder " + output.getAbsolutePath());
		}
		
		// Create provided settings
		final ProvidedSettings settings = new ProvidedSettings(version, output, run);
		
		MinecraftParser.launch(settings);
		MinecraftDownloader.launch(settings, defaultLog, logFile, skipNatives, skipAssets);
		
		if ((launch || userData != null) && authFile != null) {
			MinecraftAuthenticator.launch(settings, authFile, authenticate, authenticateType);
		}
		
		if (information) {
			MinecraftInformation.launch(settings, userData, libraryList, libraryListNatives);
		}
		
		if (launch) {
			if (demo) {
				settings.addFeature(LauncherFeatures.DEMO_USER);
			}
			
			if (settings.getVariable(LauncherVariables.AUTH_ACCESS_TOKEN) == null) {
				LOGGER.info("User authentication was not found. Set game into demo mode");
				settings.addFeature(LauncherFeatures.DEMO_USER);
			}
			
			if (customResolution) {
				settings.addFeature(LauncherFeatures.HAS_CUSTOM_RESOLUTION);
				settings.addVariable(LauncherVariables.RESOLUTION_WIDTH, width.toString());
				settings.addVariable(LauncherVariables.RESOLUTION_HEIGHT, height.toString());
			}
			MinecraftClasspathBuilder.launch(settings);
			MinecraftLauncher.launch(settings, javaExec);
		}
	}
}
