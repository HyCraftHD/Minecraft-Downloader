package net.hycrafthd.minecraft_downloader.util;

public class FileConstant {
	
	private final String constant;
	
	public FileConstant(String constant) {
		this.constant = constant;
	}
	
	public String get(String version) {
		return StringUtil.replaceVariable("version", constant, version);
	}
	
}
