package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.VersionManifestV2Json;
import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.VersionManifestV2Json.Version;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	static void launch(String version, File output) {
		final Version foundVersion = getVersionOfManifest(version);
		final ClientJson client = getClientJson(foundVersion, output);
		
		Main.LOGGER.info("Client info {}", client); // DEBUG
		
		try {
			Util.downloadFile(client.getDownloads().getClient().getUrl(), new File(output, "client.jar"), null, client.getDownloads().getClient().getSha1());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.LOGGER.info(GSON.toJson(client));
		
		// TODO download libraries and other stuff
	}
	
	private static Version getVersionOfManifest(String version) {
		final VersionManifestV2Json manifest;
		
		try {
			manifest = GSON.fromJson(Util.downloadText(VERSION_MANIFEST), VersionManifestV2Json.class);
		} catch (IOException ex) {
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
			final File file = new File(output, "client.json");
			
			Util.downloadFile(foundVersion.getUrl(), file, foundVersion.getSha1());
			
			client = GSON.fromJson(Util.readText(file), ClientJson.class);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not download / parse client json", ex);
		}
		
		return client;
	}
}
