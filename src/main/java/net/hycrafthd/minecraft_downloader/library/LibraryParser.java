package net.hycrafthd.minecraft_downloader.library;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library.Artifact;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Rule;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil.OS;

public class LibraryParser {
	
	private static final String ALLOW = "allow";
	private static final String DISALLOW = "disallow";
	
	private final Set<OS> allowedOS;
	private final Set<DownloadableFile> files;
	
	public LibraryParser(Library library) {
		allowedOS = parseRules(library.getRules());
		files = parseFiles(library);
	}
	
	private Set<OS> parseRules(List<Rule> rules) {
		// Check if no rule exist. Then library is for all os
		if (rules == null || rules.isEmpty()) {
			return OS.ALL_OS;
		}
		
		final Set<OS> os = new HashSet<>();
		
		// Check if allow rule is there.
		for (Rule rule : rules) {
			// If os is not defined in allow then add all os
			if (ALLOW.equals(rule.getAction())) {
				if (rule.getOs() != null) {
					os.add(OS.getOsByName(rule.getOs().getName()));
				} else {
					os.addAll(OS.ALL_OS);
				}
			} else if (DISALLOW.equals(rule.getAction())) {
				os.remove(OS.getOsByName(rule.getOs().getName()));
			}
		}
		return os;
	}
	
	private Set<DownloadableFile> parseFiles(Library library) {
		final Set<DownloadableFile> files = new HashSet<>();
		
		final Artifact mainArtifact = library.getDownloads().getArtifact();
		files.add(new DownloadableFile(mainArtifact.getUrl(), mainArtifact.getPath(), mainArtifact.getSize(), mainArtifact.getSha1()));
		
		return files;
	}
	
	public boolean isAllowedOnOs() {
		return allowedOS.contains(OSUtil.CURRENT_OS);
	}
	
	public Set<DownloadableFile> getFiles() {
		return files;
	}
}