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
import net.hycrafthd.minecraft_downloader.settings.LauncherFeatures;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.minecraft_downloader.util.logging.LoggingUtil;

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
		final OptionSpec<File> javaExecSpec = parser.accepts("javaExec", "Which java executable should be used to launch minecraft. If non java executable is set, the jre from this process will be used").availableIf(launchSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<Void> inlineLaunchSpec = parser.accepts("inlineLaunch", "Should minecraft be launched in the current jvm process. Ignores the jre parameter (May be buggy)").availableIf(launchSpec).availableUnless(javaExecSpec);
		final OptionSpec<File> runSpec = parser.accepts("run", "Run directory for the game").availableIf(launchSpec).requiredIf(launchSpec).withRequiredArg().ofType(File.class);
		
		final OptionSpec<Void> demoSpec = parser.accepts("demo", "Start the demo mode").availableIf(launchSpec);
		
		final OptionSpec<Integer> widthSpec = parser.accepts("width", "Width of the window").availableIf(launchSpec).withRequiredArg().ofType(Integer.class);
		final OptionSpec<Integer> heightSpec = parser.accepts("height", "Height of the window").availableIf(launchSpec).withRequiredArg().ofType(Integer.class);
		
		// Login specs
		final OptionSpec<String> usernameSpec = parser.accepts("username", "Username / Email for login").requiredIf(launchSpec).withRequiredArg();
		final OptionSpec<String> passwordSpec = parser.accepts("password", "Password for login").requiredIf(launchSpec).withRequiredArg().ofType(String.class);
		
		// Special specs
		final OptionSpec<Void> skipAssetsSpec = parser.accepts("skipAssets", "Skip the assets downloader").availableUnless(launchSpec);
		
		// Information specs
		final OptionSpec<Void> informationSpec = parser.accepts("extraInformation", "Should extra information be extracted");
		final OptionSpec<File> userDataSpec = parser.accepts("userData", "Create a file with the user information login").availableIf(informationSpec).availableIf(usernameSpec, passwordSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<File> libraryListSpec = parser.accepts("libraryList", "Create a library list file with all library excluding natives").availableIf(informationSpec).withRequiredArg().ofType(File.class);
		final OptionSpec<File> libraryListNativesSpec = parser.accepts("libraryListNatives", "Create a library list file with only native libraries").availableIf(informationSpec).withRequiredArg().ofType(File.class);
		
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
		final boolean inlineLaunch = set.has(inlineLaunchSpec);
		final File run = set.valueOf(runSpec);
		
		final boolean demo = set.has(demoSpec);
		
		final boolean customResolution = set.has(widthSpec) && set.has(heightSpec);
		final Integer width = set.valueOf(widthSpec);
		final Integer height = set.valueOf(heightSpec);
		
		final String username = set.valueOf(usernameSpec);
		final String password = set.valueOf(passwordSpec);
		
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
		MinecraftDownloader.launch(settings, skipAssets);
		
		if (launch || userData != null) {
			MinecraftClasspathBuilder.launch(settings);
			MinecraftAuthenticator.launch(settings, username, password);
		}
		
		if (information) {
			MinecraftInformation.launch(settings, userData, libraryList, libraryListNatives);
		}
		
		if (launch) {
			if (demo) {
				settings.addFeature(LauncherFeatures.DEMO_USER);
			}
			
			if (customResolution) {
				settings.addFeature(LauncherFeatures.HAS_CUSTOM_RESOLUTION);
				settings.addVariable(LauncherVariables.RESOLUTION_WIDTH, width.toString());
				settings.addVariable(LauncherVariables.RESOLUTION_HEIGHT, height.toString());
			}
			MinecraftLauncher.launch(settings, javaExec, inlineLaunch);
		}
	}
}
