package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;

/**
 * Version manifest v2 json endpoint <br>
 * See <a href=
 * "https://minecraft.gamepedia.com/Version_manifest.json">https://minecraft.gamepedia.com/Version_manifest.json</a>
 */
public class VersionManifestV2 {
	
	private Latest latest;
	private ArrayList<Version> versions;
	
	public Latest getLatest() {
		return latest;
	}
	
	public ArrayList<Version> getVersions() {
		return versions;
	}
	
	public class Latest {
		
		private String release;
		private String snapshot;
		
		public String getRelease() {
			return release;
		}
		
		public String getSnapshot() {
			return snapshot;
		}
		
	}
	
	public class Version {
		
		private String id;
		private String type;
		private String url;
		private String time;
		private String releaseTime;
		private String sha1;
		private int complianceLevel;
		
		public String getId() {
			return id;
		}
		
		public String getType() {
			return type;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getTime() {
			return time;
		}
		
		public String getReleaseTime() {
			return releaseTime;
		}
		
		public String getSha1() {
			return sha1;
		}
		
		public int getComplianceLevel() {
			return complianceLevel;
		}
	}
}
