package net.hycrafthd.minecraft_downloader.library;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class DownloadableFile {
	
	private final String url;
	private final String path;
	private final int size;
	private final String sha1;
	
	private final boolean isNative;
	private final List<String> extractExclusion;
	
	private transient File downloadedFile;
	
	public DownloadableFile(String url, String path, int size, String sha1) {
		this(url, path, size, sha1, false);
	}
	
	public DownloadableFile(String url, String path, int size, String sha1, boolean isNative) {
		this(url, path, size, sha1, isNative, Collections.emptyList());
	}
	
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extractExclusion == null) ? 0 : extractExclusion.hashCode());
		result = prime * result + (isNative ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((sha1 == null) ? 0 : sha1.hashCode());
		result = prime * result + size;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DownloadableFile other = (DownloadableFile) obj;
		if (extractExclusion == null) {
			if (other.extractExclusion != null)
				return false;
		} else if (!extractExclusion.equals(other.extractExclusion))
			return false;
		if (isNative != other.isNative)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (sha1 == null) {
			if (other.sha1 != null)
				return false;
		} else if (!sha1.equals(other.sha1))
			return false;
		if (size != other.size)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	public void setDownloadedFile(File downloadedFile) {
		this.downloadedFile = downloadedFile;
	}
	
	public boolean hasDownloadedFile() {
		return downloadedFile != null && downloadedFile.exists();
	}
	
	public File getDownloadedFile() {
		return downloadedFile;
	}
	
}
