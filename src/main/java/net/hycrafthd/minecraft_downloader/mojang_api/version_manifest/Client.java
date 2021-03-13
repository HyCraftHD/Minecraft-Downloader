package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Client {
	
	private String id;
	private ArrayList<Libary> libraries;
	
	@Override
	public String toString() {
		return "Client [id=" + id + ", libaries=" + libraries + "]";
	}
	
	public class Libary {
		
		private Downloads downloads;
		private Classifiers classifiers;
		private String name;
		private String url;
		private Natives natives;
		private Extract extract;
		private ArrayList<Rule> rules;
		
		@Override
		public String toString() {
			return "Libary [downloads=" + downloads + ", classifiers=" + classifiers + ", name=" + name + ", url=" + url + ", natives=" + natives + ", extract=" + extract + ", rules=" + rules + "]";
		}
	}
	
	public class Downloads {
		
		private Artifact artifact;
		
		@Override
		public String toString() {
			return "Downloads [artifact=" + artifact + "]";
		}
	}
	
	public class Artifact {
		
		private String path;
		private String sha1;
		private int size;
		private String url;
		
		@Override
		public String toString() {
			return "Artifact [path=" + path + ", sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
		}
	}
	
	public class Classifiers {
		
		private Artifact javadoc;
		@SerializedName(value = "natives-linux")
		private Artifact natives_linux;
		@SerializedName(value = "natives-macos")
		private Artifact natives_macos;
		@SerializedName(value = "natives-windows")
		private Artifact natives_windows;
		private Artifact sources;
		
		@Override
		public String toString() {
			return "Classifiers [javadoc=" + javadoc + ", natives_linux=" + natives_linux + ", natives_macos=" + natives_macos + ", natives_windows=" + natives_windows + ", sources=" + sources + "]";
		}
	}
	
	public class Natives {
		
		private String linux;
		private String macos;
		private String windows;
		private String osx;
		
		@Override
		public String toString() {
			return "Natives [linux=" + linux + ", macos=" + macos + ", windows=" + windows + ", osx=" + osx + "]";
		}
	}
	
	private class Extract {
		
		private ArrayList<String> exclude;
		
		@Override
		public String toString() {
			return "Extract [exclude=" + exclude + "]";
		}
	}
	
	private class Rule {
		
		private String action;
		private OS os;
		
		@Override
		public String toString() {
			return "Rule [action=" + action + ", os=" + os + "]";
		}
	}
	
	private class OS {
		
		private String name;
		
		@Override
		public String toString() {
			return "OS [name=" + name + "]";
		}
	}
}
