package net.hycrafthd.minecraft_downloader;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.VersionManifestV2;
import net.hycrafthd.minecraft_downloader.util.Util;

public class MinecraftDownloader {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	
	static void launch(String version, File output) {
		
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		try {
			final String versionManifest = Util.downloadJson(VERSION_MANIFEST);
			
			VersionManifestV2 manifest = gson.fromJson(versionManifest, VersionManifestV2.class);
			
			Main.LOGGER.info(manifest);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
