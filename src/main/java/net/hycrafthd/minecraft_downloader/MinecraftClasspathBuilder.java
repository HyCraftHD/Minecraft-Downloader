package net.hycrafthd.minecraft_downloader;

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
		
		createClassLoader(settings);
		
		Main.LOGGER.info("Finished the classpath builder");
	}
	
	private static void createClassLoader(ProvidedSettings settings) {
		Main.LOGGER.info("Find all jars and create classloader");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final List<URL> classPath = Stream.concat(Stream.of(settings.getClientJarFile(), settings.getAuthImplFile()), generatedSettings.getDownloadableFiles() //
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
		
		Main.LOGGER.debug("The classpath entries are: ");
		classPath.forEach(url -> {
			Main.LOGGER.debug(" " + url);
		});
		
		final MinecraftClassLoader classLoader = new MinecraftClassLoader(classPath.stream().toArray(URL[]::new));
		
		generatedSettings.setClassPath(classPath);
		generatedSettings.setClassLoader(classLoader);
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
