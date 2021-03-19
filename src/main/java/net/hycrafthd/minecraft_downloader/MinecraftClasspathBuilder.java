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
		
		final ClassLoader classLoader = new URLClassLoader(classPath.stream().toArray(URL[]::new));
		
		generatedSettings.setClassPath(classPath);
		generatedSettings.setClassLoader(classLoader);
	}
	
}