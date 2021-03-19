package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;

import net.hycrafthd.minecraft_downloader.launch.ArgumentsParser;
import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftLauncher {
	
	static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start minecraft");
		
		setVariables(settings);
	}
	
	private static void setVariables(ProvidedSettings settings) {
		Main.LOGGER.info("Set variables for start");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final CurrentClientJson client = generatedSettings.getClientJson();
		
		settings.addVariable(LauncherVariables.VERSION_NAME, client.getId());
		settings.addVariable(LauncherVariables.VERSION_TYPE, client.getType());
		
		settings.addVariable(LauncherVariables.GAME_DIRECTORY, settings.getRunDirectory());
		
		settings.addVariable(LauncherVariables.ASSET_ROOT, settings.getAssetsDirectory());
		settings.addVariable(LauncherVariables.ASSET_INDEX_NAME, client.getAssetIndex().getId());
		
		settings.addVariable(LauncherVariables.LAUNCHER_NAME, "Minecraft Downloader");
		settings.addVariable(LauncherVariables.LAUNCHER_VERSION, "1.0.0");
		
		settings.addVariable(LauncherVariables.NATIVE_DIRECTORY, settings.getNativesDirectory());
		settings.addVariable(LauncherVariables.CLASSPATH, generatedSettings.getClassPath().stream().map(File::getAbsolutePath).collect(Collectors.joining(";")));
	}
	
	private static void launchInline(CurrentClientJson client, List<LibraryParser> parsedLibraries, ArgumentsParser parser, File output) {
		
		System.setProperty("os.name", "Windows 10");
		System.setProperty("os.version", "10.0");
		
		// Use org.lwjgl.librarypath instead of java.library.path (only works for lwjgl natives)
		System.setProperty("org.lwjgl.librarypath", new File(output, "natives").toString());
		
		System.setProperty("minecraft.launcher.brand", "Minecraft-Downloader");
		System.setProperty("minecraft.launcher.version", "1.0.0");
		
		final List<URL> libraries = parsedLibraries.stream() //
				.flatMap(libraryParser -> libraryParser.getFiles().stream()) //
				.filter(downloadableFile -> !downloadableFile.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(downloadableFile -> {
					try {
						return downloadableFile.getDownloadedFile().toURI().toURL();
					} catch (MalformedURLException ex) {
						throw new IllegalStateException("Cannot get url from file " + downloadableFile.getDownloadedFile(), ex);
					}
				}) //
				.collect(Collectors.toList());
		
		final URL[] classPath = new URL[libraries.size() + 1];
		
		final File file = new File(output, "client.jar");
		
		try {
			classPath[0] = file.toURI().toURL();
		} catch (MalformedURLException ex) {
			throw new IllegalStateException("Cannot create url from client jar file " + file, ex);
		}
		
		for (int index = 0; index < libraries.size(); index++) {
			classPath[index + 1] = libraries.get(index);
		}
		
		final URLClassLoader classloader = new URLClassLoader(classPath, null);
		
		try {
			
			Main.LOGGER.info("Library path is: " + System.getProperty("java.library.path"));
			
			final Class<?> mainClass = Class.forName(client.getMainClass(), true, classloader);
			final Method entryPoint = mainClass.getMethod("main", String[].class);
			
			entryPoint.invoke(null, new Object[] { parser.getGameArgs() });
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to run minecraft", ex);
		}
	}
}
