package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.legacy.IconAssetFix;
import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson.AssetJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.AssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.DownloadsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.DownloadsJson.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LoggingJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LoggingJson.LoggingClientJson.LoggingFileJson;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.minecraft_downloader.util.StringUtil;

public class MinecraftDownloader {
	
	public static void launch(ProvidedSettings settings, boolean defaultLog, File logFile, boolean skipNatives, boolean skipAssets) {
		Main.LOGGER.info("Start downloading library and asset files");
		
		downloadClient(settings);
		downloadLibraries(settings);
		if (!skipNatives) {
			extractNatives(settings);
		} else {
			Main.LOGGER.info("Skipped extracting natives");
		}
		if (!skipAssets) {
			downloadAssets(settings);
			chooseLogger(settings, defaultLog, logFile);
		} else {
			Main.LOGGER.info("Skipped assets and logger");
		}
		
		Main.LOGGER.info("Finished downloading library and asset files");
	}
	
	private static void downloadClient(ProvidedSettings settings) {
		Main.LOGGER.info("Download client jar and mappings");
		
		final DownloadsJson downloads = settings.getGeneratedSettings().getClientJson().getDownloads();
		
		final ClientJson clientJar = downloads.getClient();
		final ClientJson clientMappings = downloads.getClientMappings();
		
		FileUtil.downloadFileException(clientJar.getUrl(), settings.getClientJarFile(), clientJar.getSize(), clientJar.getSha1(), "Failed to download client jar");
		if (clientMappings != null) {
			FileUtil.downloadFileException(clientMappings.getUrl(), settings.getClientMappingsFile(), clientMappings.getSize(), clientMappings.getSha1(), "Failed to download client mappings");
		} else {
			Main.LOGGER.info("Skip client mappings as they are not avaiable for this minecraft version");
		}
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
									for (final String exclusion : downloadableFile.getExtractExclusion()) {
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
									} catch (final IOException ex) {
										throw new IllegalStateException("Could not extract jar entry " + jarEntry.getName(), ex);
									}
									
									file.setLastModified(jarEntry.getLastModifiedTime().toMillis());
								});
								
						Main.LOGGER.debug("Extracted files from {}", downloadedFile);
					} catch (final IOException ex) {
						throw new IllegalStateException("Could not extract native library of file " + downloadedFile, ex);
					}
				});
	}
	
	private static void downloadAssets(ProvidedSettings settings) {
		Main.LOGGER.info("Download assets");
		
		final File assets = settings.getAssetsDirectory();
		
		final AssetIndexJson assetIndex = settings.getGeneratedSettings().getClientJson().getAssetIndex();
		
		final CurrentAssetIndexJson index;
		
		try {
			final File indexFile = new File(assets, "indexes" + Constants.FILE_SEPERATOR + assetIndex.getId() + ".json");
			
			FileUtil.downloadFile(assetIndex.getUrl(), indexFile, assetIndex.getSize(), assetIndex.getSha1());
			
			index = Constants.GSON.fromJson(FileUtil.readText(indexFile), CurrentAssetIndexJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse asset index", ex);
		}
		
		index.getAssets().values().parallelStream().forEach(assetObject -> {
			final String first2HashLetters = StringUtil.first2Letters(assetObject.getHash());
			
			final String url = Constants.RESOURCES + Constants.URL_SEPERATOR + first2HashLetters + Constants.URL_SEPERATOR + assetObject.getHash();
			final File file = new File(assets, "objects" + Constants.FILE_SEPERATOR + first2HashLetters + Constants.FILE_SEPERATOR + assetObject.getHash());
			
			FileUtil.downloadFileException(url, file, assetObject.getSize(), assetObject.getHash(), "Failed to download asset");
		});
		
		if (index.isMapToResources() || index.isVirtual()) {
			Main.LOGGER.info("Legacy assets found. Reconstruct assets");
			
			final File resources = new File(settings.getRunDirectory(), "resources");
			final File virtualAssets = new File(assets, "legacy_virtual" + Constants.FILE_SEPERATOR + assetIndex.getId());
			
			final File unhashedFolder;
			
			if (index.isMapToResources()) {
				unhashedFolder = resources;
			} else {
				unhashedFolder = virtualAssets;
			}
			
			FileUtil.createFolders(unhashedFolder);
			
			index.getAssets().entrySet().parallelStream().forEach(entry -> {
				final String name = entry.getKey();
				final AssetJson assetObject = entry.getValue();
				
				final String first2HashLetters = StringUtil.first2Letters(assetObject.getHash());
				
				final File hashedFile = new File(assets, "objects" + Constants.FILE_SEPERATOR + first2HashLetters + Constants.FILE_SEPERATOR + assetObject.getHash());
				final File unhashedFile = new File(unhashedFolder, name);
				FileUtil.createParentFolders(unhashedFile);
				
				try {
					Files.copy(hashedFile.toPath(), unhashedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (final IOException ex) {
					throw new IllegalStateException("Could not copy file " + hashedFile + " to virtual assets index " + unhashedFile, ex);
				}
				Main.LOGGER.debug("Copied file from {} to {}", hashedFile, unhashedFile);
			});
			
			// Fix pre-1.6 icons
			IconAssetFix.fix(assetIndex, virtualAssets);
			
			settings.getGeneratedSettings().setVirtualAssets(virtualAssets);
		}
	}
	
	private static void chooseLogger(ProvidedSettings settings, boolean defaultLog, File logFile) {
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		final LoggingJson logging = generatedSettings.getClientJson().getLogging();
		
		if (logging != null) {
			if (defaultLog) {
				downloadLogger(settings);
			} else if (logFile != null) {
				Main.LOGGER.info("Use supplied log4j file {}", logFile);
				generatedSettings.setLogFile(logFile);
			} else {
				extractShippedLogger(settings);
			}
		} else {
			Main.LOGGER.info("Skip log4j file as it is not available for this minecraft version");
		}
	}
	
	private static void downloadLogger(ProvidedSettings settings) {
		Main.LOGGER.info("Download log4j file");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		final LoggingJson logging = generatedSettings.getClientJson().getLogging();
		
		final LoggingFileJson loggingFile = logging.getClient().getFile();
		
		final File logFile = new File(settings.getAssetsDirectory(), "log_configs" + Constants.FILE_SEPERATOR + loggingFile.getId());
		FileUtil.downloadFileException(loggingFile.getUrl(), logFile, loggingFile.getSize(), loggingFile.getSha1(), "Failed to download logger file");
		
		generatedSettings.setLogFile(logFile);
	}
	
	private static void extractShippedLogger(ProvidedSettings settings) {
		Main.LOGGER.info("Extract shipped log4j file");
		
		final File logFile = new File(settings.getOutputDirectory(), Constants.SHIPPED_LOG4J_CONFIG);
		
		try (final InputStream inputStream = MinecraftDownloader.class.getResourceAsStream(Constants.URL_SEPERATOR + Constants.SHIPPED_LOG4J_CONFIG); //
				final OutputStream outputStream = new FileOutputStream(logFile)) {
			FileUtil.copy(inputStream, outputStream, new byte[2048]);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not extract shipped log4j file", ex);
		}
		
		settings.getGeneratedSettings().setLogFile(logFile);
	}
}
