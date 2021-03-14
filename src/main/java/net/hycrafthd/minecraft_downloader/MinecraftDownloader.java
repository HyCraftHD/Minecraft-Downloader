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

import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments.Value;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads.Client;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json.Version;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ArgumentsSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ValueSerializer;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Arguments.class, new ArgumentsSerializer()).registerTypeHierarchyAdapter(Value.class, new ValueSerializer()).create();
	
	public static final String CLIENT_JSON = "client.json";
	public static final String CLIENT_JAR = "client.jar";
	public static final String CLIENT_MAPPINGS = "client.txt";
	
	public static final String LIBRARIES = "libraries";
	public static final String NATIVES = "natives";
	
	static void launch(String version, File output) {
		final Version foundVersion = getVersionOfManifest(version);
		final ClientJson client = getClientJson(foundVersion, output);
		
		final List<LibraryParser> parsedLibraries = parseLibraries(client);
		
		downloadClient(client, output);
		downloadLibraries(parsedLibraries, output);
		extractNatives(parsedLibraries, output);
	}
	
	private static Version getVersionOfManifest(String version) {
		final VersionManifestV2Json manifest;
		
		try {
			manifest = GSON.fromJson(Util.downloadText(VERSION_MANIFEST), VersionManifestV2Json.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse version manifest json", ex);
		}
		
		final Optional<Version> foundVersionOptional = manifest.getVersions().stream().filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();
		
		if (!foundVersionOptional.isPresent()) {
			throw new IllegalArgumentException("The requested version {} was not found in the version manifest json");
		}
		
		return foundVersionOptional.get();
	}
	
	private static ClientJson getClientJson(Version foundVersion, File output) {
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
		final Downloads downloads = client.getDownloads();
		
		final Client clientJar = downloads.getClient();
		final Client clientMappings = downloads.getClientMappings();
		
		Util.downloadFileException(clientJar.getUrl(), new File(output, CLIENT_JAR), clientJar.getSize(), clientJar.getSha1(), "Failed to download client jar");
		Util.downloadFileException(clientMappings.getUrl(), new File(output, CLIENT_MAPPINGS), clientMappings.getSize(), clientMappings.getSha1(), "Failed to download client mappings");
	}
	
	private static List<LibraryParser> parseLibraries(ClientJson client) {
		return client.getLibraries().stream() //
				.map(LibraryParser::new) //
				.filter(LibraryParser::isAllowedOnOs) //
				.collect(Collectors.toList());
	}
	
	private static void downloadLibraries(List<LibraryParser> parsedLibraries, File output) {
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
		final File libraries = new File(output, LIBRARIES);
		final File natives = new File(output, NATIVES);
		natives.mkdir();
		
		parsedLibraries.stream() //
				.flatMap(e -> e.getFiles().stream()) //
				.filter(e -> e.isNative()) //
				.forEach(downloadableFile -> {
					File file = new File(libraries, downloadableFile.getPath());
					
					try (final JarFile jarFile = new JarFile(file)) {
						
						try (final Stream<JarEntry> entryStream = jarFile.stream()) {
							entryStream.filter(e -> !e.isDirectory()).filter(jarEntry -> {
								for (String string : downloadableFile.getExtractExclusion()) {
									if (jarEntry.getName().startsWith(string)) {
										return false;
									}
								}
								return true;
								
							}).forEach(finalJarEntry -> {
								
								File fileOut = new File(natives, finalJarEntry.getName());
								
								final File parent = fileOut.getParentFile();
								if (parent != null) {
									parent.mkdirs();
								}
								final byte buffer[] = new byte[8192];
								try (final InputStream inputStream = jarFile.getInputStream(finalJarEntry); //
										final OutputStream outputStream = new FileOutputStream(fileOut)) {
									int count;
									while ((count = inputStream.read(buffer)) != -1) {
										outputStream.write(buffer, 0, count);
									}
								} catch (IOException ex) {
									ex.printStackTrace();
								}
								
							});
						}
						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					
				});
	}
}
