package net.hycrafthd.minecraft_downloader.settings;

import java.util.List;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class GeneratedSettings {
	
	private CurrentClientJson clientJson;
	
	private List<DownloadableFile> downloadableFiles;
	
	public void setClientJson(CurrentClientJson clientJson) {
		this.clientJson = clientJson;
	}
	
	public CurrentClientJson getClientJson() {
		if (clientJson == null) {
			throw new IllegalStateException("Client json is not set");
		}
		return clientJson;
	}
	
	public void setDownloadableFiles(List<DownloadableFile> downloadableFiles) {
		this.downloadableFiles = downloadableFiles;
	}
	
	public List<DownloadableFile> getDownloadableFiles() {
		if (downloadableFiles == null) {
			throw new IllegalStateException("Downloadable files list is not set");
		}
		return downloadableFiles;
	}
	
}
