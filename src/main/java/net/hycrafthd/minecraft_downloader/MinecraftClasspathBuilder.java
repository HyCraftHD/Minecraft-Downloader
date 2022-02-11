package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftClasspathBuilder {
	
	public static void launch(ProvidedSettings settings, boolean skipClasspathShortening) {
		Main.LOGGER.info("Start the classpath builder");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final Set<File> classPath = Stream.concat(Stream.of(settings.getClientJarFile()), generatedSettings.getDownloadableFiles() //
				.stream() //
				.filter(downloadableFile -> !downloadableFile.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(DownloadableFile::getDownloadedFile)) //
				.collect(Collectors.toSet());
		
		Main.LOGGER.debug("The classpath entries are: ");
		classPath.forEach(file -> {
			Main.LOGGER.debug(" " + file);
		});
		
		if (!skipClasspathShortening) {
			generateShortClasspathJar(settings, classPath);
		} else {
			generatedSettings.setClassPath(classPath);
		}
		
		Main.LOGGER.info("Finished the classpath builder");
	}
	
	private static void generateShortClasspathJar(ProvidedSettings settings, Set<File> classpath) {
		Main.LOGGER.info("Shortening classpath");
		
		final Manifest manifest = new Manifest();
		
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, classpath.stream().map(File::toURI).map(URI::toString).collect(Collectors.joining(" ")));
		
		try (final JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(settings.getClientClasspathJarFile()), manifest)) {
			outputStream.putNextEntry(new ZipEntry("META-INF/"));
		} catch (final IOException ex) {
			throw new IllegalStateException("Cannot create short class path jar", ex);
		}
		
		settings.getGeneratedSettings().setClassPath(Set.of(settings.getClientClasspathJarFile()));
	}
}
