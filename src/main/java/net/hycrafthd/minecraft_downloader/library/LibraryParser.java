package net.hycrafthd.minecraft_downloader.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.BaseOsRuleJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.DownloadsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.DownloadsJson.ArtifactJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.LibraryRuleJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.NativesJson;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil.OS;

public class LibraryParser {
	
	private static final String ALLOW = "allow";
	private static final String DISALLOW = "disallow";
	
	private final Set<OS> allowedOS;
	private final Set<DownloadableFile> files;
	
	public LibraryParser(LibraryJson library) {
		allowedOS = parseRules(library.getRules());
		files = parseFiles(library);
	}
	
	private Set<OS> parseRules(List<LibraryRuleJson> rules) {
		// Check if no rule exist. Then library is for all os
		if (rules == null || rules.isEmpty()) {
			return OS.ALL_OS;
		}
		
		final Set<OS> os = new HashSet<>();
		
		// Check if allow rule is there.
		for (BaseOsRuleJson rule : rules) {
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
	
	private Set<DownloadableFile> parseFiles(LibraryJson library) {
		final Set<DownloadableFile> files = new HashSet<>();
		
		final DownloadsJson downloads = library.getDownloads();
		
		// Add main artifact
		final ArtifactJson mainArtifact = downloads.getArtifact();
		files.add(new DownloadableFile(mainArtifact.getUrl(), mainArtifact.getPath(), mainArtifact.getSize(), mainArtifact.getSha1()));
		
		// Check for native library
		final NativesJson natives = library.getNatives();
		if (natives != null) {
			final String classifierName = natives.getNatives().get(OSUtil.CURRENT_OS.getName());
			if (classifierName != null) {
				final ArtifactJson nativeArtifact = downloads.getClassifiers().getClassifiers().get(classifierName);
				
				if (nativeArtifact != null) {
					final List<String> extractExclusion;
					
					if (library.getExtract() != null && library.getExtract().getExclude() != null) {
						extractExclusion = library.getExtract().getExclude();
					} else {
						extractExclusion = new ArrayList<>();
					}
					
					files.add(new DownloadableFile(nativeArtifact.getUrl(), nativeArtifact.getPath(), nativeArtifact.getSize(), nativeArtifact.getSha1(), true, extractExclusion));
				}
			}
		}
		
		return files;
	}
	
	public boolean isAllowedOnOs() {
		return allowedOS.contains(OSUtil.CURRENT_OS);
	}
	
	public Set<DownloadableFile> getFiles() {
		return files;
	}
}
