package net.hycrafthd.minecraft_downloader;

import java.io.File;
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

		try {
			final String versionManifest = Util.downloadJson(VERSION_MANIFEST);

			VersionManifestV2 manifest = GSON.fromJson(versionManifest, VersionManifestV2.class);

			final Optional<Version> foundVersionOptional = manifest.getVersions().stream()
					.filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();

			if (foundVersionOptional.isPresent()) {
				final Version foundVersion = foundVersionOptional.get();
				Main.LOGGER.info(foundVersion);

				final String clientJson = Util.downloadJson(foundVersion.getUrl(), foundVersion.getSha1());

				Client client = GSON.fromJson(clientJson, Client.class);

				Main.LOGGER.info(clientJson);

				Main.LOGGER.info(client);

				Main.LOGGER.info(clientJson.length());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
