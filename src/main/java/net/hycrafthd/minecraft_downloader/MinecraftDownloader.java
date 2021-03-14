package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.library.LibraryParser;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments.Value;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Downloads.Client;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library.Artifact;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestV2Json.Version;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ArgumentsSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ValueSerializer;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Arguments.class, new ArgumentsSerializer()).registerTypeHierarchyAdapter(Value.class, new ValueSerializer()).create();
	
	public static final String CLIENT_JSON = "client.json";
	public static final String CLIENT_JAR = "client.jar";
	public static final String CLIENT_MAPPINGS = "client.txt";
	
	public static final String LIBRARIES = "libraries";
	
	static void launch(String version, File output) {
		final Version foundVersion = getVersionOfManifest(version);
		final ClientJson client = getClientJson(foundVersion, output);
		
		downloadClient(client, output);
		downloadLibraries(client, output);
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
		
		try {
			Util.downloadFile(clientJar.getUrl(), new File(output, CLIENT_JAR), clientJar.getSize(), clientJar.getSha1());
			Util.downloadFile(clientMappings.getUrl(), new File(output, CLIENT_MAPPINGS), clientMappings.getSize(), clientMappings.getSha1());
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download client", ex);
		}
	}
	
	private static void downloadLibraries(ClientJson client, File output) {
		final File libraries = new File(output, LIBRARIES);
		libraries.mkdir();
		
		client.getLibraries().stream().filter(e -> e.getRules() != null && e.getRules().size() > 0) // Libaries with rules
				.map(LibraryParser::new) // Map to Libary with List of Allowed OS�s
				.filter(e -> e.getOSList().contains(OSUtil.CURRENT_OS)) // Filter for Current OS
				.map(e -> e.getLibary()).forEach(e -> { // Map back to Libary
					
					// Download Libaries
					final ClientJson.Library.Artifact artifact = e.getDownloads().getArtifact();
					try {
						Util.downloadFile(artifact.getUrl(), new File(libraries, artifact.getPath()), artifact.getSize(), artifact.getSha1());
					} catch (final IOException ex) {
						// ex.printStackTrace(); // TODO Exeption
						throw new IllegalStateException("Libary download failed");
					}
					
					// Download Natives
					try {
						if (e.getNatives() != null) {
							if (e.getNatives().getLinux() != null && OSUtil.CURRENT_OS.equals(OSUtil.OS.LINUX)) {
								final Artifact art = e.getDownloads().getClassifiers().getNativesLinux();
								Util.downloadFile(art.getUrl(), new File(libraries, art.getPath()), art.getSize(), art.getSha1());
							}
							if (e.getNatives().getOsx() != null && OSUtil.CURRENT_OS.equals(OSUtil.OS.OSX)) {
								final Artifact art = e.getDownloads().getClassifiers().getNativesMacos();
								Util.downloadFile(art.getUrl(), new File(libraries, art.getPath()), art.getSize(), art.getSha1());
							}
							if (e.getNatives().getWindows() != null && OSUtil.CURRENT_OS.equals(OSUtil.OS.WINDOWS)) {
								final Artifact art = e.getDownloads().getClassifiers().getNativesWindows();
								Util.downloadFile(art.getUrl(), new File(libraries, art.getPath()), art.getSize(), art.getSha1());
							}
						}
					} catch (final IOException ex) {
						// ex.printStackTrace(); // TODO Exeption
						throw new IllegalStateException("Natives download failed");
					}
				});
		
	}
}
