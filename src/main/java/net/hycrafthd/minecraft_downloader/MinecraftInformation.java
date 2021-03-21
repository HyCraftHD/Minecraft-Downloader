package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class MinecraftInformation {
	
	static void launch(ProvidedSettings settings, File libraryList, File libraryListNatives) {
		Main.LOGGER.info("Start information extractor");
		
		if (libraryList != null) {
			writeLibraryListInfo(settings, libraryList);
		}
		
		if (libraryListNatives != null) {
			writeLibraryListNativesInfo(settings, libraryListNatives);
		}
		
		Main.LOGGER.info("Finished information extractor");
	}
	
	private static void writeLibraryListInfo(ProvidedSettings settings, File libraryList) {
		Main.LOGGER.info("Extract library list");
		
		final Stream<String> libraries = settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.stream() //
				.filter(file -> !file.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(file -> file.getDownloadedFile().getAbsolutePath());
		
		FileUtil.createParentFolders(libraryList);
		
		try {
			FileUtil.writeText(libraries, libraryList);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not write library list info file", ex);
		}
	}
	
	private static void writeLibraryListNativesInfo(ProvidedSettings settings, File libraryList) {
		Main.LOGGER.info("Extract library list");
		
		final Stream<String> libraries = settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.stream() //
				.filter(DownloadableFile::isNative) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(file -> file.getDownloadedFile().getAbsolutePath());
		
		FileUtil.createParentFolders(libraryList);
		
		try {
			FileUtil.writeText(libraries, libraryList);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not write library list native info file", ex);
		}
	}
	
}
