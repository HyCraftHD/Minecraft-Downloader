package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestJson;
import net.hycrafthd.minecraft_downloader.mojang_api.VersionManifestJson.VersionJson;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class MinecraftParser {
	
	static CurrentClientJson launch(String version, File output) {
		Main.LOGGER.info("Start parsing json files");
		
		final VersionJson foundVersion = getVersionOfManifest(version);
		final CurrentClientJson client = getClientJson(foundVersion, output);
		
		Main.LOGGER.info("Finished parsing json files");
		return client;
	}
	
	private static VersionJson getVersionOfManifest(String version) {
		Main.LOGGER.info("Download and load version manifest");
		
		final VersionManifestJson manifest;
		
		try {
			manifest = Constants.GSON.fromJson(FileUtil.downloadText(Constants.VERSION_MANIFEST), VersionManifestJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse version manifest json", ex);
		}
		
		final Optional<VersionJson> foundVersionOptional = manifest.getVersions().stream().filter(manifestVersion -> manifestVersion.getId().equals(version)).findAny();
		
		if (!foundVersionOptional.isPresent()) {
			throw new IllegalArgumentException("The requested version {} was not found in the version manifest json");
		}
		
		return foundVersionOptional.get();
	}
	
	private static CurrentClientJson getClientJson(VersionJson foundVersion, File output) {
		Main.LOGGER.info("Download and extract client json");
		
		final CurrentClientJson client;
		
		try {
			final File file = new File(output, Constants.CLIENT_JSON);
			
			FileUtil.downloadFile(foundVersion.getUrl(), file, foundVersion.getSha1());
			
			client = Constants.GSON.fromJson(FileUtil.readText(file), CurrentClientJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse client json", ex);
		}
		
		return client;
	}
	
}
