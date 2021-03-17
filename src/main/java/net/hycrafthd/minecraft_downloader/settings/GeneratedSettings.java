package net.hycrafthd.minecraft_downloader.settings;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class GeneratedSettings {
	
	private CurrentClientJson clientJson;
	
	public void setClientJson(CurrentClientJson clientJson) {
		this.clientJson = clientJson;
	}
	
	public CurrentClientJson getClientJson() {
		if (clientJson == null) {
			throw new IllegalStateException("Client json is null");
		}
		return clientJson;
	}
	
}
