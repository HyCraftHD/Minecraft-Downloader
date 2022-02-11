package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JavaVersionManifestJson {
	
	protected PlatformJson gamecore;
	protected PlatformJson linux;
	@SerializedName("linux-i386")
	protected PlatformJson linux_i386;
	@SerializedName("mac-os")
	protected PlatformJson mac_os;
	@SerializedName("windows-x64")
	protected PlatformJson windows_x64;
	@SerializedName("windows-x86")
	protected PlatformJson windows_x86;
	
	public JavaVersionManifestJson(PlatformJson gamecore, PlatformJson linux, PlatformJson linux_i386, PlatformJson mac_os, PlatformJson windows_x64, PlatformJson windows_x86) {
		this.gamecore = gamecore;
		this.linux = linux;
		this.linux_i386 = linux_i386;
		this.mac_os = mac_os;
		this.windows_x64 = windows_x64;
		this.windows_x86 = windows_x86;
	}
	
	public PlatformJson getGamecore() {
		return gamecore;
	}
	
	public PlatformJson getLinux() {
		return linux;
	}
	
	public PlatformJson getLinuxI386() {
		return linux_i386;
	}
	
	public PlatformJson getMacOs() {
		return mac_os;
	}
	
	public PlatformJson getWindowsX64() {
		return windows_x64;
	}
	
	public PlatformJson getWindowsX86() {
		return windows_x86;
	}
	
	@Override
	public String toString() {
		return "JavaVersionManifestJson [gamecore=" + gamecore + ", linux=" + linux + ", linux_i386=" + linux_i386 + ", mac_os=" + mac_os + ", windows_x64=" + windows_x64 + ", windows_x86=" + windows_x86 + "]";
	}
	
	public static class PlatformJson {
		
		@SerializedName("java-runtime-alpha")
		protected ArrayList<JavaRuntimeJson> javaRuntimeAlpha;
		@SerializedName("java-runtime-beta")
		protected ArrayList<JavaRuntimeJson> javaRuntimeBeta;
		@SerializedName("jre-legacy")
		protected ArrayList<JavaRuntimeJson> jreLegacy;
		@SerializedName("minecraft-java-exe")
		protected ArrayList<JavaRuntimeJson> minecraftJavaExe;
		
		public PlatformJson(ArrayList<JavaRuntimeJson> javaRuntimeAlpha, ArrayList<JavaRuntimeJson> javaRuntimeBeta, ArrayList<JavaRuntimeJson> jreLegacy, ArrayList<JavaRuntimeJson> minecraftJavaExe) {
			this.javaRuntimeAlpha = javaRuntimeAlpha;
			this.javaRuntimeBeta = javaRuntimeBeta;
			this.jreLegacy = jreLegacy;
			this.minecraftJavaExe = minecraftJavaExe;
		}
		
		public ArrayList<JavaRuntimeJson> getJavaRuntimeAlpha() {
			return javaRuntimeAlpha;
		}
		
		public ArrayList<JavaRuntimeJson> getJavaRuntimeBeta() {
			return javaRuntimeBeta;
		}
		
		public ArrayList<JavaRuntimeJson> getJreLegacy() {
			return jreLegacy;
		}
		
		public ArrayList<JavaRuntimeJson> getMinecraftJavaExe() {
			return minecraftJavaExe;
		}
		
		@Override
		public String toString() {
			return "PlatformJson [javaRuntimeAlpha=" + javaRuntimeAlpha + ", javaRuntimeBeta=" + javaRuntimeBeta + ", jreLegacy=" + jreLegacy + ", minecraftJavaExe=" + minecraftJavaExe + "]";
		}
		
		public static class JavaRuntimeJson {
			
			protected AvailabilityJson availability;
			protected ManifestJson manifest;
			protected VersionJson version;
			
			public JavaRuntimeJson(AvailabilityJson availability, ManifestJson manifest, VersionJson version) {
				this.availability = availability;
				this.manifest = manifest;
				this.version = version;
			}
			
			public AvailabilityJson getAvailability() {
				return availability;
			}
			
			public ManifestJson getManifest() {
				return manifest;
			}
			
			public VersionJson getVersion() {
				return version;
			}
			
			@Override
			public String toString() {
				return "JavaRuntimeJson [availability=" + availability + ", manifest=" + manifest + ", version=" + version + "]";
			}
			
			public static class AvailabilityJson {
				
				protected int group;
				protected int progress;
				
				public AvailabilityJson(int group, int progress) {
					this.group = group;
					this.progress = progress;
				}
				
				public int getGroup() {
					return group;
				}
				
				public int getProgress() {
					return progress;
				}
				
				@Override
				public String toString() {
					return "AvailabilityJson [group=" + group + ", progress=" + progress + "]";
				}
				
			}
			
			public static class ManifestJson {
				
				protected String sha1;
				protected int size;
				protected String url;
				
				public ManifestJson(String sha1, int size, String url) {
					this.sha1 = sha1;
					this.size = size;
					this.url = url;
				}
				
				public String getSha1() {
					return sha1;
				}
				
				public int getSize() {
					return size;
				}
				
				public String getUrl() {
					return url;
				}
				
				@Override
				public String toString() {
					return "ManifestJson [sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
				}
				
			}
			
			public static class VersionJson {
				
				protected String name;
				protected String released;
				
				public VersionJson(String name, String released) {
					this.name = name;
					this.released = released;
				}
				
				public String getName() {
					return name;
				}
				
				public String getReleased() {
					return released;
				}
				
				@Override
				public String toString() {
					return "VersionJson [name=" + name + ", released=" + released + "]";
				}
				
			}
		}
	}
}
