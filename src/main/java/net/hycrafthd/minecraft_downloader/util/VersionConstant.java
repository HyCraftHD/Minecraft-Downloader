package net.hycrafthd.minecraft_downloader.util;

public class VersionConstant {
	
	private final String constant;
	
	public VersionConstant(String constant) {
		this.constant = constant;
	}
	
	public String get(String version) {
		return StringUtil.replaceVariable("version", constant, version);
	}
	
	@Override
	public String toString() {
		return constant;
	}
	
}
