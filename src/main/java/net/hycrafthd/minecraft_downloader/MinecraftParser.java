package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestJson;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestJson.VersionJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class MinecraftParser {
	
	public static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start parsing json files");
		
		parseClientJson(extractVersionOfManifest(settings.getVersion()), settings);
		parseLibraries(settings);
		
		Main.LOGGER.info("Finished parsing json files");
	}
	
	private static VersionJson extractVersionOfManifest(String version) {
		Main.LOGGER.info("Download and parse version manifest");
		
		final VersionManifestJson manifest;
		
		try {
			manifest = Constants.GSON.fromJson(FileUtil.downloadText(Constants.VERSION_MANIFEST), VersionManifestJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse version manifest json", ex);
		}
		
		final Optional<VersionJson> foundVersionOptional = manifest.getVersions().stream().filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();
		
		if (!foundVersionOptional.isPresent()) {
			throw new IllegalArgumentException("The requested version " + version + " was not found in the version manifest json");
		}
		
		return foundVersionOptional.get();
	}
	
	private static void parseClientJson(VersionJson foundVersion, ProvidedSettings settings) {
		Main.LOGGER.info("Download and parse client json");
		
		final CurrentClientJson client;
		
		try {
			final File file = settings.getClientJsonFile();
			
			FileUtil.downloadFile(foundVersion.getUrl(), file, foundVersion.getSha1());
			
			client = Constants.GSON.fromJson(FileUtil.readText(file), CurrentClientJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse client json", ex);
		}
		
		settings.getGeneratedSettings().setClientJson(client);
	}
	
	private static void parseLibraries(ProvidedSettings settings) {
		Main.LOGGER.info("Parse required libraries");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final List<DownloadableFile> downloadableFiles = generatedSettings.getClientJson() //
				.getLibraries() //
				.stream() //
				.map(LibraryParser::new) //
				.filter(LibraryParser::isAllowedOnOs) //
				.flatMap(parser -> parser.getFiles().stream()) //
				.distinct() //
				.collect(Collectors.toList());
		
		generatedSettings.setDownloadableFiles(downloadableFiles);
	}
	
}
