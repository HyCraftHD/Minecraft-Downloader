package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.LauncherVariables;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class MinecraftInformation {
	
	public static void launch(ProvidedSettings settings, File userData, File libraryList, File libraryListNatives) {
		Main.LOGGER.info("Start information extractor");
		
		if (userData != null) {
			writeUserDataInfo(settings, userData);
		}
		
		if (libraryList != null) {
			writeLibraryListInfo(settings, libraryList);
		}
		
		if (libraryListNatives != null) {
			writeLibraryListNativesInfo(settings, libraryListNatives);
		}
		
		Main.LOGGER.info("Finished information extractor");
	}
	
	private static void writeUserDataInfo(ProvidedSettings settings, File userData) {
		Main.LOGGER.info("Extract user information");
		
		final String playerName = settings.getVariable(LauncherVariables.AUTH_PLAYER_NAME);
		final String uuid = settings.getVariable(LauncherVariables.AUTH_UUID);
		final String accessToken = settings.getVariable(LauncherVariables.AUTH_ACCESS_TOKEN);
		final String userType = settings.getVariable(LauncherVariables.USER_TYPE);
		
		try {
			FileUtil.writeText(Stream.of(playerName, uuid, accessToken, userType), userData);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not write access token file", ex);
		}
	}
	
	private static void writeLibraryListInfo(ProvidedSettings settings, File libraryList) {
		Main.LOGGER.info("Extract library list");
		
		final Stream<String> libraries = settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.stream() //
				.filter(file -> !file.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(file -> file.getDownloadedFile().getAbsolutePath()) //
				.distinct();
		
		FileUtil.createParentFolders(libraryList);
		
		try {
			FileUtil.writeText(libraries, libraryList);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not write library list info file", ex);
		}
	}
	
	private static void writeLibraryListNativesInfo(ProvidedSettings settings, File libraryList) {
		Main.LOGGER.info("Extract native library list");
		
		final Stream<String> libraries = settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.stream() //
				.filter(DownloadableFile::isNative) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(file -> file.getDownloadedFile().getAbsolutePath()) //
				.distinct();
		
		FileUtil.createParentFolders(libraryList);
		
		try {
			FileUtil.writeText(libraries, libraryList);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not write library list native info file", ex);
		}
	}
	
}
