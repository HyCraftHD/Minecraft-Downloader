package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.Map;

import com.google.gson.annotations.JsonAdapter;

import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.IndexSerializer;

/**
 * Asset index json endpoint <br>
 * See <a href=
 * "https://minecraft-de.gamepedia.com/Standard-Ressourcen#.minecraft.2Fassets">https://minecraft-de.gamepedia.com/Standard-Ressourcen#.minecraft.2Fassets</a>
 * There is unfortunately only a german version on the minecraft wiki
 */
@JsonAdapter(IndexSerializer.class)
public class CurrentAssetIndexJson {
	
	private Map<String, AssetJson> assets;
	
	public CurrentAssetIndexJson(Map<String, AssetJson> assets) {
		this.assets = assets;
	}
	
	public Map<String, AssetJson> getAssets() {
		return assets;
	}
	
	@Override
	public String toString() {
		return "CurrentAssetIndexJson [assets=" + assets + "]";
	}
	
	public static class AssetJson {
		
		private String hash;
		private int size;
		
		public AssetJson(String hash, int size) {
			this.hash = hash;
			this.size = size;
		}
		
		public String getHash() {
			return hash;
		}
		
		public int getSize() {
			return size;
		}
		
		@Override
		public String toString() {
			return "AssetJson [hash=" + hash + ", size=" + size + "]";
		}
	}
}
