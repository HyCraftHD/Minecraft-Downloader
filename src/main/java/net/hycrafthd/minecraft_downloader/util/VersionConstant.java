package net.hycrafthd.minecraft_downloader.util;

public record VersionConstant(String constant) {
	
	public String get(String version) {
		return StringUtil.replaceVariable("version", constant, version);
	}
	
	@Override
	public String toString() {
		return constant;
	}
	
}
