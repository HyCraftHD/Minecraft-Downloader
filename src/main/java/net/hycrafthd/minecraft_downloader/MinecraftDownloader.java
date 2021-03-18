package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.AssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.DownloadsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.DownloadsJson.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LoggingJson.LoggingClientJson.LoggingFileJson;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.minecraft_downloader.util.StringUtil;

public class MinecraftDownloader {
	
	static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start downloading library and asset files");
		
		downloadClient(settings);
		downloadLibraries(settings);
		extractNatives(settings);
		downloadAssets(settings);
		downloadLogger(settings);
		
		Main.LOGGER.info("Finished downloading library and asset files");
	}
	
	private static void downloadClient(ProvidedSettings settings) {
		Main.LOGGER.info("Download client jar and mappings");
		
		final DownloadsJson downloads = settings.getGeneratedSettings().getClientJson().getDownloads();
		
		final ClientJson clientJar = downloads.getClient();
		final ClientJson clientMappings = downloads.getClientMappings();
		
		FileUtil.downloadFileException(clientJar.getUrl(), settings.getClientJarFile(), clientJar.getSize(), clientJar.getSha1(), "Failed to download client jar");
		FileUtil.downloadFileException(clientMappings.getUrl(), settings.getClientMappingsFile(), clientMappings.getSize(), clientMappings.getSha1(), "Failed to download client mappings");
	}
	
	private static void downloadLibraries(ProvidedSettings settings) {
		Main.LOGGER.info("Download required libraries");
		
		final File libraries = settings.getLibrariesDirectory();
		
		settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.parallelStream() //
				.forEach(downloadableFile -> {
					final File file = new File(libraries, downloadableFile.getPath());
					
					FileUtil.downloadFileException(downloadableFile.getUrl(), file, downloadableFile.getSize(), downloadableFile.getSha1(), "Failed to download library");
					downloadableFile.setDownloadedFile(file);
				});
	}
	
	private static void extractNatives(ProvidedSettings settings) {
		Main.LOGGER.info("Extract native files from native jars");
		
		final File natives = settings.getNativesDirectory();
		
		settings.getGeneratedSettings() //
				.getDownloadableFiles() //
				.stream() //
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
									
									FileUtil.createParentFolders(file);
									
									Main.LOGGER.debug("Extract entry {} of file {} to {}", jarEntry.getName(), downloadedFile, file);
									
									try (final InputStream inputStream = jarFile.getInputStream(jarEntry); //
											final OutputStream outputStream = new FileOutputStream(file)) {
										FileUtil.copy(inputStream, outputStream, buffer);
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
	
	public static void downloadAssets(ProvidedSettings settings) {
		Main.LOGGER.info("Download assets");
		
		final File assets = settings.getAssetsDirectory();
		
		final AssetIndexJson assetIndex = settings.getGeneratedSettings().getClientJson().getAssetIndex();
		
		final CurrentAssetIndexJson index;
		
		try {
			final File indexFile = new File(assets, "indexes" + Constants.FILE_SEPERATOR + assetIndex.getId() + ".json");
			
			FileUtil.downloadFile(assetIndex.getUrl(), indexFile, assetIndex.getSize(), assetIndex.getSha1());
			
			index = Constants.GSON.fromJson(FileUtil.readText(indexFile), CurrentAssetIndexJson.class);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not download / parse asset index", ex);
		}
		
		index.getAssets().values().parallelStream().forEach(assetObject -> {
			final String first2HashLetters = StringUtil.first2Letters(assetObject.getHash());
			
			final String url = Constants.RESOURCES + Constants.URL_SEPERATOR + first2HashLetters + Constants.URL_SEPERATOR + assetObject.getHash();
			final File file = new File(assets, "objects" + Constants.FILE_SEPERATOR + first2HashLetters + Constants.FILE_SEPERATOR + assetObject.getHash());
			
			FileUtil.downloadFileException(url, file, assetObject.getSize(), assetObject.getHash(), "Failed to download asset");
		});
	}
	
	private static void downloadLogger(ProvidedSettings settings) {
		Main.LOGGER.info("Download logger file");
		
		final File assets = settings.getAssetsDirectory();
		
		final LoggingFileJson loggingFile = settings.getGeneratedSettings().getClientJson().getLogging().getClient().getFile();
		
		FileUtil.downloadFileException(loggingFile.getUrl(), new File(assets, "log_configs" + Constants.FILE_SEPERATOR + loggingFile.getId()), loggingFile.getSize(), loggingFile.getSha1(), "Failed to download logger file");
	}
}
