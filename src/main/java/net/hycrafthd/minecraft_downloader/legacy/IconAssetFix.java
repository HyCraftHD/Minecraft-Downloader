package net.hycrafthd.minecraft_downloader.legacy;

import java.io.File;
import java.util.Map;

import net.hycrafthd.minecraft_downloader.Constants;
import net.hycrafthd.minecraft_downloader.Main;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson.AssetJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.AssetIndexJson;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.minecraft_downloader.util.StringUtil;

public class IconAssetFix {
	
	public static void fix(AssetIndexJson assetIndex, File virtualAssets) {
		if ("pre-1.6".equals(assetIndex.getId())) {
			Main.LOGGER.info("Fix pre-1.6 icons");
			
			FileUtil.createFolders(virtualAssets);
			
			// Map with icons to download
			Map.of( //
					"icons/icon_16x16.png", new AssetJson("bdf48ef6b5d0d23bbb02e17d04865216179f510a", 3665), //
					"icons/icon_32x32.png", new AssetJson("92750c5f93c312ba9ab413d546f32190c56d6f1f", 5362), //
					"icons/minecraft.icns", new AssetJson("991b421dfd401f115241601b2b373140a8d78572", 114786) //
			).entrySet().parallelStream().forEach(entry -> {
				final String name = entry.getKey();
				final AssetJson assetObject = entry.getValue();
				
				final String first2HashLetters = StringUtil.first2Letters(assetObject.getHash());
				
				final String url = Constants.RESOURCES + Constants.URL_SEPERATOR + first2HashLetters + Constants.URL_SEPERATOR + assetObject.getHash();
				final File file = new File(virtualAssets, name);
				
				FileUtil.downloadFileException(url, file, assetObject.getSize(), assetObject.getHash(), "Failed to download asset");
			});
		}
	}
	
}
