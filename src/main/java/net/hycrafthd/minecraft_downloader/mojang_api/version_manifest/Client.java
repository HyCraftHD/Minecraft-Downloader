package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Client {

	private ArrayList<Libary> libaries;

	public class Libary {
		private Downloads downloads;
		private Classifiers classifiers;
		private String name;
		private String url;
		private Natives natives;
		private Extract extract;
		private ArrayList<Rule> rules;
	}

	public class Downloads {
		private Artifact artifact;
	}

	public class Artifact {
		private String path;
		private String sha1;
		private int size;
		private String url;
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
	}

	public class Natives {
		private String linux;
		private String macos;
		private String windows;
		private String osx;
	}

	private class Extract {
		private ArrayList<String> exclude;
	}

	private class Rule {
		private String action;
		private OS os;
	}

	private class OS {
		private String name;
	}

}
