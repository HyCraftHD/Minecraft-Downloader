package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.ArrayList;

/**
 * Version manifest v2 json endpoint <br>
 * See <a href=
 * "https://minecraft.gamepedia.com/Version_manifest.json">https://minecraft.gamepedia.com/Version_manifest.json</a>
 */
public class VersionManifestV2Json {
	
	protected LatestJson latest;
	protected ArrayList<VersionJson> versions;
	
	public VersionManifestV2Json(LatestJson latest, ArrayList<VersionJson> versions) {
		this.latest = latest;
		this.versions = versions;
	}
	
	public LatestJson getLatest() {
		return latest;
	}
	
	public ArrayList<VersionJson> getVersions() {
		return versions;
	}
	
	@Override
	public String toString() {
		return "VersionManifestV2Json [latest=" + latest + ", versions=" + versions + "]";
	}
	
	public static class LatestJson {
		
		protected String release;
		protected String snapshot;
		
		public LatestJson(String release, String snapshot) {
			this.release = release;
			this.snapshot = snapshot;
		}
		
		public String getRelease() {
			return release;
		}
		
		public String getSnapshot() {
			return snapshot;
		}
		
		@Override
		public String toString() {
			return "LatestJson [release=" + release + ", snapshot=" + snapshot + "]";
		}
		
	}
	
	public static class VersionJson {
		
		protected String id;
		protected String type;
		protected String url;
		protected String time;
		protected String releaseTime;
		protected String sha1;
		protected int complianceLevel;
		
		public VersionJson(String id, String type, String url, String time, String releaseTime, String sha1, int complianceLevel) {
			this.id = id;
			this.type = type;
			this.url = url;
			this.time = time;
			this.releaseTime = releaseTime;
			this.sha1 = sha1;
			this.complianceLevel = complianceLevel;
		}
		
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
		
		@Override
		public String toString() {
			return "VersionJson [id=" + id + ", type=" + type + ", url=" + url + ", time=" + time + ", releaseTime=" + releaseTime + ", sha1=" + sha1 + ", complianceLevel=" + complianceLevel + "]";
		}
		
	}
}
