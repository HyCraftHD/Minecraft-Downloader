package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.Map;

public class Index {
	
	public Map<String, AssetObject> assets;
	
	public Index(Map<String, AssetObject> assets) {
		this.assets = assets;
	}
	
	public Map<String, AssetObject> getAssets() {
		return assets;
	}
	
	@Override
	public String toString() {
		return "Index [assets=" + assets + "]";
	}
	
	public static class AssetObject {
		
		private String hash;
		private String size;
		
		public AssetObject(String hash, String size) {
			this.hash = hash;
			this.size = size;
		}
		
		public String getHash() {
			return hash;
		}
		
		public String getSize() {
			return size;
		}
		
		@Override
		public String toString() {
			return "AssetObject [hash=" + hash + ", size=" + size + "]";
		}
	}
}
