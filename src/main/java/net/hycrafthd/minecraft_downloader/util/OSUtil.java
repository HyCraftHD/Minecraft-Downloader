package net.hycrafthd.minecraft_downloader.util;

import java.util.Locale;

public class OSUtil {
	
	public static final OS CURRENT_OS;
	
	static {
		final String system = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		
		if (system.contains("win")) {
			CURRENT_OS = OS.WINDOWS;
		} else if (system.contains("nix") || system.contains("nux") || system.contains("aix")) {
			CURRENT_OS = OS.LINUX;
		} else if (system.contains("mac")) {
			CURRENT_OS = OS.OSX;
		} else {
			CURRENT_OS = OS.LINUX;
		}
	}
	
	public enum OS {
		
		WINDOWS("windows", "windows"),
		LINUX("linux", "linux"),
		OSX("osx", "macos");
		
		private final String name;
		private final String classifierName;
		
		private OS(String name, String classifierName) {
			this.name = name;
			this.classifierName = classifierName;
		}
		
		public String getName() {
			return name;
		}
		
		public String getClassifierName() {
			return classifierName;
		}
	}
}
