package net.hycrafthd.minecraft_downloader.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library.Artifact;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library.Downloads;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Library.Natives;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.BaseOsRule;
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
	
	private Set<OS> parseRules(List<BaseOsRule> rules) {
		// Check if no rule exist. Then library is for all os
		if (rules == null || rules.isEmpty()) {
			return OS.ALL_OS;
		}
		
		final Set<OS> os = new HashSet<>();
		
		// Check if allow rule is there.
		for (BaseOsRule rule : rules) {
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
		
		final Downloads downloads = library.getDownloads();
		
		// Add main artifact
		final Artifact mainArtifact = downloads.getArtifact();
		files.add(new DownloadableFile(mainArtifact.getUrl(), mainArtifact.getPath(), mainArtifact.getSize(), mainArtifact.getSha1()));
		
		// Check for native library
		final Natives natives = library.getNatives();
		if (natives != null) {
			// TODO make os not variables but a list / map with a custom gson serializer
			
			final Artifact nativeArtifact;
			
			if (OSUtil.CURRENT_OS == OS.WINDOWS && natives.getWindows() != null) {
				nativeArtifact = downloads.getClassifiers().getNativesWindows();
			} else if (OSUtil.CURRENT_OS == OS.LINUX && natives.getLinux() != null) {
				nativeArtifact = downloads.getClassifiers().getNativesLinux();
			} else if (OSUtil.CURRENT_OS == OS.OSX && natives.getOsx() != null) {
				nativeArtifact = downloads.getClassifiers().getNativesMacos();
			} else {
				nativeArtifact = null;
			}
			
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
		
		return files;
	}
	
	public boolean isAllowedOnOs() {
		return allowedOS.contains(OSUtil.CURRENT_OS);
	}
	
	public Set<DownloadableFile> getFiles() {
		return files;
	}
}
