package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.Map;

import com.google.gson.annotations.JsonAdapter;

import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.CurrentJavaVersionSerializer;

@JsonAdapter(CurrentJavaVersionSerializer.class)
public class CurrentJavaVersionJson {
	
	protected Map<String, FileJson> files;
	
	public CurrentJavaVersionJson(Map<String, FileJson> files) {
		this.files = files;
	}
	
	public Map<String, FileJson> getFiles() {
		return files;
	}
	
	@Override
	public String toString() {
		return "CurrentJavaVersionJson [files=" + files + "]";
	}
	
	public static class FileJson {
		
		protected DownloadsJson downloads;
		protected boolean executable;
		protected String type;
		
		public FileJson(DownloadsJson downloads, boolean executable, String type) {
			this.downloads = downloads;
			this.executable = executable;
			this.type = type;
		}
		
		public DownloadsJson getDownloads() {
			return downloads;
		}
		
		public boolean isExecutable() {
			return executable;
		}
		
		public String getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return "FileJson [downloads=" + downloads + ", executable=" + executable + ", type=" + type + "]";
		}
		
		public static class DownloadsJson {
			
			protected DownloadJson lzma;
			protected DownloadJson raw;
			
			public DownloadsJson(DownloadJson lzma, DownloadJson raw) {
				this.lzma = lzma;
				this.raw = raw;
			}
			
			public DownloadJson getLzma() {
				return lzma;
			}
			
			public DownloadJson getRaw() {
				return raw;
			}
			
			@Override
			public String toString() {
				return "DownloadsJson [lzma=" + lzma + ", raw=" + raw + "]";
			}
			
			public static class DownloadJson {
				
				protected String sha1;
				protected int size;
				protected String url;
				
				public DownloadJson(String sha1, int size, String url) {
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
					return "DownloadJson [sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
				}
			}
		}
	}
}
