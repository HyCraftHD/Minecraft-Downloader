package net.hycrafthd.minecraft_downloader.settings;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson;

public class GeneratedSettings {
	
	private CurrentClientJson clientJson;
	
	private List<DownloadableFile> downloadableFiles;
	
	private List<URL> classPath;
	
	private ClassLoader classLoader;
	
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
	
	public void setClassPath(List<URL> classPath) {
		this.classPath = classPath;
	}
	
	public List<URL> getClassPath() {
		if (classPath == null) {
			throw new IllegalStateException("Classpath is not set");
		}
		return Collections.unmodifiableList(classPath);
	}
	
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			throw new IllegalStateException("Classloader is not set");
		}
		return classLoader;
	}
	
}
