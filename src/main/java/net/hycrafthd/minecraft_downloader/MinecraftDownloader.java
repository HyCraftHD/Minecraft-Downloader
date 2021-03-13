package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.Client;
import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.VersionManifestV2;
import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.VersionManifestV2.Version;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	static void launch(String version, File output) {
		
		final VersionManifestV2 manifest;
		
		try {
			manifest = GSON.fromJson(Util.downloadJson(VERSION_MANIFEST), VersionManifestV2.class);
		} catch (IOException ex) {
			Main.LOGGER.fatal("Could not download / parse version manifest json", ex);
			return;
		}
		
		final Optional<Version> foundVersionOptional = manifest.getVersions().stream().filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();
		
		if (!foundVersionOptional.isPresent()) {
			Main.LOGGER.error("The requested version {} was not found in the version manifest json", version);
			return;
		}
		
		final Version foundVersion = foundVersionOptional.get();
		
		final Client client;
		
		try {
			client = GSON.fromJson(Util.downloadJson(foundVersion.getUrl(), foundVersion.getSha1()), Client.class);
		} catch (IOException ex) {
			Main.LOGGER.fatal("Could not download / parse version manifest json", ex);
			return;
		}
		
		// TODO download libraries and other stuff
	}
}
