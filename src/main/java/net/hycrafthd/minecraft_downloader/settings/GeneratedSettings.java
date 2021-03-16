package net.hycrafthd.minecraft_downloader.settings;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class GeneratedSettings {
	
	private CurrentClientJson clientJson;
	
	public void setClientJson(CurrentClientJson clientJson) {
		this.clientJson = clientJson;
	}
	
	public boolean hasClientJson() {
		return clientJson != null;
	}
	
	public CurrentClientJson getClientJson() {
		return clientJson;
	}
	
}
