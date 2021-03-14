package net.hycrafthd.minecraft_downloader.library;

import java.util.List;

public class DownloadableFile {
	
	private final String url;
	private final String path;
	private final int size;
	private final String sha1;
	
	private final boolean isNative;
	private final List<String> extractExclusion;
	
	public DownloadableFile(String url, String path, int size, String sha1, boolean isNative, List<String> extractExclusion) {
		this.url = url;
		this.path = path;
		this.size = size;
		this.sha1 = sha1;
		this.isNative = isNative;
		this.extractExclusion = extractExclusion;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getSha1() {
		return sha1;
	}
	
	public boolean isNative() {
		return isNative;
	}
	
	public List<String> getExtractExclusion() {
		return extractExclusion;
	}
	
}
