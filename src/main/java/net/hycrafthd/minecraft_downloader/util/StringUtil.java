package net.hycrafthd.minecraft_downloader.util;

public class StringUtil {
	
	public static String replaceVersion(String string, String value) {
		return replaceVariable("version", string, value);
	}
	
	public static String replaceVariable(String variable, String string, String value) {
		return string.replace("${" + variable + "}", value);
	}
	
}
