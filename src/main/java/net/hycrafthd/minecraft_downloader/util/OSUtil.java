package net.hycrafthd.minecraft_downloader.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OSUtil {
	
	public static final OS CURRENT_OS;
	public static final String CURRENT_VERSION;
	public static final String CURRENT_ARCH;
	
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
		
		CURRENT_VERSION = System.getProperty("os.version").toLowerCase(Locale.ROOT);
		CURRENT_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
	}
	
	public enum OS {
		
		WINDOWS("windows", "windows"),
		LINUX("linux", "linux"),
		OSX("osx", "macos");
		
		public static final Set<OS> ALL_OS = Collections.unmodifiableSet(Arrays.stream(values()).collect(Collectors.toSet()));
		private static final Map<String, OS> NAME_TO_OS = ALL_OS.stream().collect(Collectors.toMap(OS::getName, Function.identity()));
		
		public static final OS getOsByName(String name) {
			return NAME_TO_OS.get(name);
		}
		
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
