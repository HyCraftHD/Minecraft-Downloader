package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;

import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.AssetIndex;
import net.hycrafthd.minecraft_downloader.mojang_api.Index;
import net.hycrafthd.minecraft_downloader.util.Util;

public class DownloadAssets {
	
	public static final String RESOURCES = "https://resources.download.minecraft.net";
	
	public static final String ASSETS = "assets";
	
	public static void downloadAssets(ClientJson client, File output) {
		final File assets = new File(output, ASSETS);
		assets.mkdir();
		
		final AssetIndex assetIndex = client.getAssetIndex();
		
		final Index index;
		
		try {
			final File indexFile = new File(assets, "indexes/" + assetIndex.getId() + ".json");
			
			Util.downloadFile(assetIndex.getUrl(), indexFile, assetIndex.getSize(), assetIndex.getSha1());
			
			client = MinecraftDownloader.GSON.fromJson(Util.readText(indexFile), ClientJson.class);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not download asset index", ex);
		}
		
		Main.LOGGER.info(client.getAssetIndex());
		Main.LOGGER.info(client.getAssets());
	}
	
}
