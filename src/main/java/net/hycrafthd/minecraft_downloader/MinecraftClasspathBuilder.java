package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftClasspathBuilder {
	
	static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start the classpath builder");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final List<URL> classPath = Stream.concat(Stream.of(settings.getClientJarFile()), generatedSettings.getDownloadableFiles() //
				.stream() //
				.filter(downloadableFile -> !downloadableFile.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(DownloadableFile::getDownloadedFile)) //
				.map(file -> {
					try {
						return file.toURI().toURL();
					} catch (MalformedURLException ex) {
						throw new IllegalStateException("Cannot get url from file " + file, ex);
					}
				}).collect(Collectors.toList());
		
		final MinecraftClassLoader classLoader = new MinecraftClassLoader(classPath.stream().toArray(URL[]::new));
		
		// TODO REMOVE (ONLY DEBUG)
		try {
			classLoader.addURL(new File("D:\\Programmieren\\Java\\Forge\\U Team\\1.16.5 Projects\\Headless-Minecraft\\Minecraft-Downloader\\minecraft-auth\\build\\libs\\minecraft-auth.jar").toURI().toURL());
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		
		generatedSettings.setClassPath(classPath);
		generatedSettings.setClassLoader(classLoader);
		
		Main.LOGGER.info("Finished the classpath builder");
	}
	
	private static class MinecraftClassLoader extends URLClassLoader {
		
		static {
			ClassLoader.registerAsParallelCapable();
		}
		
		public MinecraftClassLoader(URL[] urls) {
			super(urls, ClassLoader.getSystemClassLoader());
		}
		
		@Override
		public void addURL(URL url) {
			super.addURL(url);
		}
		
	}
	
}
