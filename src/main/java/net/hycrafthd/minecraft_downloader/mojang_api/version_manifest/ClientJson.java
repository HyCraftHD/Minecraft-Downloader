package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ClientJson {
	
	private Downloads downloads;
	private ArrayList<Libary> libraries;
	
	public Downloads getDownloads() {
		return downloads;
	}
	
	public ArrayList<Libary> getLibraries() {
		return libraries;
	}
	
	@Override
	public String toString() {
		return "Client [downloads=" + downloads + ", libraries=" + libraries + "]";
	}
	
	public class Downloads {
		
		private Client client;
		private Client client_mappings;
		private Client server;
		private Client server_mappings;
		
		public Client getClient() {
			return client;
		}
		
		public Client getClientMappings() {
			return client_mappings;
		}
		
		public Client getServer() {
			return server;
		}
		
		public Client getServerMappings() {
			return server_mappings;
		}
		
		@Override
		public String toString() {
			return "Downloads [client=" + client + ", client_mappings=" + client_mappings + ", server=" + server + ", server_mappings=" + server_mappings + "]";
		}
		
		public class Client {
			
			private String sha1;
			private int size;
			
			private String url;
			
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
				return "Client [sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
			}
			
		}
	}
	
	public class Libary {
		
		private Classifiers classifiers;
		private Downloads downloads;
		private Extract extract;
		private String name;
		private Natives natives;
		private ArrayList<Rule> rules;
		private String url;
		
		public Classifiers getClassifiers() {
			return classifiers;
		}
		
		public Downloads getDownloads() {
			return downloads;
		}
		
		public Extract getExtract() {
			return extract;
		}
		
		public String getName() {
			return name;
		}
		
		public Natives getNatives() {
			return natives;
		}
		
		public ArrayList<Rule> getRules() {
			return rules;
		}
		
		public String getUrl() {
			return url;
		}
		
		@Override
		public String toString() {
			return "Libary [downloads=" + downloads + ", classifiers=" + classifiers + ", name=" + name + ", url=" + url + ", natives=" + natives + ", extract=" + extract + ", rules=" + rules + "]";
		}
		
		public class Artifact {
			
			private String path;
			private String sha1;
			private int size;
			private String url;
			
			public String getPath() {
				return path;
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
			
			public Artifact getJavadoc() {
				return javadoc;
			}
			
			public Artifact getNativesLinux() {
				return natives_linux;
			}
			
			public Artifact getNativesMacos() {
				return natives_macos;
			}
			
			public Artifact getNativesWindows() {
				return natives_windows;
			}
			
			public Artifact getSources() {
				return sources;
			}
			
			@Override
			public String toString() {
				return "Classifiers [javadoc=" + javadoc + ", natives_linux=" + natives_linux + ", natives_macos=" + natives_macos + ", natives_windows=" + natives_windows + ", sources=" + sources + "]";
			}
		}
		
		public class Downloads {
			
			private Artifact artifact;
			
			public Artifact getArtifact() {
				return artifact;
			}
			
			@Override
			public String toString() {
				return "Downloads [artifact=" + artifact + "]";
			}
		}
		
		public class Extract {
			
			private ArrayList<String> exclude;
			
			public ArrayList<String> getExclude() {
				return exclude;
			}
			
			@Override
			public String toString() {
				return "Extract [exclude=" + exclude + "]";
			}
		}
		
		public class Natives {
			
			private String linux;
			private String macos;
			private String osx;
			private String windows;
			
			public String getLinux() {
				return linux;
			}
			
			public String getMacos() {
				return macos;
			}
			
			public String getOsx() {
				return osx;
			}
			
			public String getWindows() {
				return windows;
			}
			
			@Override
			public String toString() {
				return "Natives [linux=" + linux + ", macos=" + macos + ", windows=" + windows + ", osx=" + osx + "]";
			}
		}
		
		public class Rule {
			
			private String action;
			private OS os;
			
			public String getAction() {
				return action;
			}
			
			public OS getOs() {
				return os;
			}
			
			@Override
			public String toString() {
				return "Rule [action=" + action + ", os=" + os + "]";
			}
			
			public class OS {
				
				private String name;
				
				public String getName() {
					return name;
				}
				
				@Override
				public String toString() {
					return "OS [name=" + name + "]";
				}
			}
		}
	}
	
}
