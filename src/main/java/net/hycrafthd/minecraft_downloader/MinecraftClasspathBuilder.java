package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftClasspathBuilder {
	
	public static void launch(ProvidedSettings settings) {
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
		
		generatedSettings.setClassPath(classPath);
		
		Main.LOGGER.info("Finished the classpath builder");
	}
}
