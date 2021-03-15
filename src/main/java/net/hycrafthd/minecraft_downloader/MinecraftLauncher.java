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
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftLauncher {
	
	static void launch(CurrentClientJson client, List<LibraryParser> parsedLibraries, File output) {
		Main.LOGGER.info("Start minecraft");
		
		final ProvidedSettings settings = new ProvidedSettings();
		
		settings.addVariable(LauncherVariables.AUTH_PLAYER_NAME, "HyCraftHD");
		settings.addVariable(LauncherVariables.AUTH_UUID, "d9202ce0f6c14dc193412d9091764808");
		settings.addVariable(LauncherVariables.AUTH_ACCESS_TOKEN, "xyz-doesn-not-matter");
		settings.addVariable(LauncherVariables.USER_TYPE, "mojang");
		
		settings.addVariable(LauncherVariables.VERSION_NAME, client.getId());
		settings.addVariable(LauncherVariables.VERSION_TYPE, client.getType());
		
		settings.addVariable(LauncherVariables.GAME_DIRECTORY, new File(output, "game").toString());
		
		settings.addVariable(LauncherVariables.ASSET_ROOT, new File(output, "assets").toString());
		settings.addVariable(LauncherVariables.ASSET_INDEX_NAME, client.getAssetIndex().getId());
		
		final ArgumentsParser parser = new ArgumentsParser(client.getArguments(), settings);
		
		launchInline(client, parsedLibraries, parser, output);
		
		Main.LOGGER.info("Stopping minecraft");
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
