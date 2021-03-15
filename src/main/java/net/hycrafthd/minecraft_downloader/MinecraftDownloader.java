package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.AssetIndex;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads.Client;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json.VersionJson;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	public static final String RESOURCES = "https://resources.download.minecraft.net";
	
	public static final String FILE_SEPERATOR = File.separator;
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static final String CLIENT_JSON = "client.json";
	public static final String CLIENT_JAR = "client.jar";
	public static final String CLIENT_MAPPINGS = "client.txt";
	
	public static final String LIBRARIES = "libraries";
	public static final String NATIVES = "natives";
	public static final String ASSETS = "assets";
	
	static void launch(String version, File output) {
		final VersionJson foundVersion = getVersionOfManifest(version);
		final ClientJson client = getClientJson(foundVersion, output);
		
		final List<LibraryParser> parsedLibraries = parseLibraries(client);
		
		downloadClient(client, output);
		downloadLibraries(parsedLibraries, output);
		extractNatives(parsedLibraries, output);
		downloadAssets(client, output);
		
		Main.LOGGER.info(GSON.toJson(client.getArguments()));
	}
	
	private static VersionJson getVersionOfManifest(String version) {
		Main.LOGGER.info("Download and load version manifest");
		
		final VersionManifestV2Json manifest;
		
		try {
			manifest = GSON.fromJson(Util.downloadText(VERSION_MANIFEST), VersionManifestV2Json.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse version manifest json", ex);
		}
		
		final Optional<VersionJson> foundVersionOptional = manifest.getVersions().stream().filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();
		
		if (!foundVersionOptional.isPresent()) {
			throw new IllegalArgumentException("The requested version {} was not found in the version manifest json");
		}
		
		return foundVersionOptional.get();
	}
	
	private static ClientJson getClientJson(VersionJson foundVersion, File output) {
		Main.LOGGER.info("Download and extract client json");
		
		final ClientJson client;
		
		try {
			final File file = new File(output, CLIENT_JSON);
			
			Util.downloadFile(foundVersion.getUrl(), file, foundVersion.getSha1());
			
			client = GSON.fromJson(Util.readText(file), ClientJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse client json", ex);
		}
		
		return client;
	}
	
	private static void downloadClient(ClientJson client, File output) {
		Main.LOGGER.info("Download client jar and mappings");
		
		final Downloads downloads = client.getDownloads();
		
		final Client clientJar = downloads.getClient();
		final Client clientMappings = downloads.getClientMappings();
		
		Util.downloadFileException(clientJar.getUrl(), new File(output, CLIENT_JAR), clientJar.getSize(), clientJar.getSha1(), "Failed to download client jar");
		Util.downloadFileException(clientMappings.getUrl(), new File(output, CLIENT_MAPPINGS), clientMappings.getSize(), clientMappings.getSha1(), "Failed to download client mappings");
	}
	
	private static List<LibraryParser> parseLibraries(ClientJson client) {
		Main.LOGGER.info("Parse required libraries");
		
		return client.getLibraries().stream() //
				.map(LibraryParser::new) //
				.filter(LibraryParser::isAllowedOnOs) //
				.collect(Collectors.toList());
	}
	
	private static void downloadLibraries(List<LibraryParser> parsedLibraries, File output) {
		Main.LOGGER.info("Download required libraries");
		
		final File libraries = new File(output, LIBRARIES);
		libraries.mkdir();
		
		parsedLibraries.parallelStream() //
				.flatMap(parser -> parser.getFiles().stream()) //
				.forEach(downloadableFile -> {
					final File file = new File(libraries, downloadableFile.getPath());
					
					Util.downloadFileException(downloadableFile.getUrl(), file, downloadableFile.getSize(), downloadableFile.getSha1(), "Failed to download library");
					downloadableFile.setDownloadedFile(file);
				});
	}
	
	private static void extractNatives(List<LibraryParser> parsedLibraries, File output) {
		Main.LOGGER.info("Extract native files from native jars");
		
		final File natives = new File(output, NATIVES);
		natives.mkdir();
		
		parsedLibraries.stream() //
				.flatMap(parser -> parser.getFiles().stream()) //
				.filter(DownloadableFile::isNative) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.forEach(downloadableFile -> {
					final File downloadedFile = downloadableFile.getDownloadedFile();
					
					try (final JarFile jarFile = new JarFile(downloadedFile); //
							final Stream<JarEntry> entryStream = jarFile.stream()) {
						
						Main.LOGGER.debug("Try to extract files from {}", downloadedFile);
						
						final byte buffer[] = new byte[8192];
						
						entryStream //
								.filter(jarEntry -> !jarEntry.isDirectory()) //
								.filter(jarEntry -> {
									for (String exclusion : downloadableFile.getExtractExclusion()) {
										if (jarEntry.getName().startsWith(exclusion)) {
											return false;
										}
									}
									return true;
								}).forEach(jarEntry -> {
									final File file = new File(natives, jarEntry.getName());
									
									Util.createParentFolders(file);
									
									Main.LOGGER.debug("Extract entry {} of file {} to {}", jarEntry.getName(), downloadedFile, file);
									
									try (final InputStream inputStream = jarFile.getInputStream(jarEntry); //
											final OutputStream outputStream = new FileOutputStream(file)) {
										Util.copy(inputStream, outputStream, buffer);
									} catch (IOException ex) {
										throw new IllegalStateException("Could not extract jar entry " + jarEntry.getName(), ex);
									}
									
									file.setLastModified(jarEntry.getLastModifiedTime().toMillis());
								});
								
						Main.LOGGER.debug("Extracted files from {}", downloadedFile);
					} catch (IOException ex) {
						throw new IllegalStateException("Could not extract native library of file " + downloadedFile, ex);
					}
				});
	}
	
	public static void downloadAssets(ClientJson client, File output) {
		Main.LOGGER.info("Download assets");
		
		final File assets = new File(output, ASSETS);
		assets.mkdir();
		
		final AssetIndex assetIndex = client.getAssetIndex();
		
		final CurrentAssetIndexJson index;
		
		try {
			final File indexFile = new File(assets, "indexes" + FILE_SEPERATOR + assetIndex.getId() + ".json");
			
			Util.downloadFile(assetIndex.getUrl(), indexFile, assetIndex.getSize(), assetIndex.getSha1());
			
			index = GSON.fromJson(Util.readText(indexFile), CurrentAssetIndexJson.class);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not download / parse asset index", ex);
		}
		
		index.getAssets().values().parallelStream().forEach(assetObject -> {
			final String first2HashLetters = Util.first2Letters(assetObject.getHash());
			
			final String url = RESOURCES + "/" + first2HashLetters + "/" + assetObject.getHash();
			final File file = new File(assets, "objects" + FILE_SEPERATOR + first2HashLetters + FILE_SEPERATOR + assetObject.getHash());
			
			Util.downloadFileException(url, file, assetObject.getSize(), assetObject.getHash(), "Failed to download asset");
		});
	}
}
