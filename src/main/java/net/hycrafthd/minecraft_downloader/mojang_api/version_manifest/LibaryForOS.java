package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.ClientJson.Libary;
import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.ClientJson.Rule;
import net.hycrafthd.minecraft_downloader.util.OSUtil;

public class LibaryForOS {
	
	private List<OSUtil.OS> OS = new ArrayList<>();
	
	private Libary libary;
	
	public LibaryForOS(Libary libary) {
		this.libary = libary;
		if (libary.getRules().size() != 1 && libary.getRules().size() != 2) {
			throw new IllegalStateException("This Rule is not in thr correct format! Only 1 and 2 Rules are allowed");
		}
		parseRules(libary.getRules());
	}
	
	public Libary getLibary() {
		return libary;
	}
	
	public List<OSUtil.OS> getOSList() {
		return OS;
	}
	
	private void parseRules(ArrayList<Rule> rules) {
		// Case allow
		rules.stream().filter(e -> e.getAction().equalsIgnoreCase("allow")).forEach(e -> {
			if (e.getOs() == null) { // All Allowed
				OS = new ArrayList<>(Arrays.asList(OSUtil.OS.values()));
			} else { // Specific Allowed
				OS.add(getOSFromString(e.getOs().getName()));
			}
		});
		
		// Case disallow
		rules.stream().filter(e -> e.getAction().equalsIgnoreCase("disallow")).forEach(e -> {
			OS.remove(getOSFromString(e.getOs().getName()));
		});
	}
	
	private OSUtil.OS getOSFromString(String in) {
		for (OSUtil.OS item : OSUtil.OS.values()) {
			if (item.getName().equals(in)) {
				return item;
			}
		}
		return null;
	}
}
