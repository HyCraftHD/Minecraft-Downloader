package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.ClientJson.Libary.Rule;

public class ClientJson {
	
	private Arguments arguments;
	private Downloads downloads;
	private ArrayList<Libary> libraries;
	
	public Arguments getArguments() {
		return arguments;
	}
	
	public Downloads getDownloads() {
		return downloads;
	}
	
	public ArrayList<Libary> getLibraries() {
		return libraries;
	}
	
	@Override
	public String toString() {
		return "ClientJson [arguments=" + arguments + ", downloads=" + downloads + ", libraries=" + libraries + "]";
	}
	
	public static class Arguments {
		
		private ArrayList<String> gameArguments;
		private ArrayList<ConditionalGameArgument> conditionalGameArguments;
		
		private ArrayList<String> jvmArguments;
		private ArrayList<ConditionalJvmArgument> conditionaljvmArguments;
		
		public Arguments(ArrayList<String> gameArguments, ArrayList<ConditionalGameArgument> conditionalGameArguments, ArrayList<String> jvmArguments, ArrayList<ConditionalJvmArgument> conditionaljvmArguments) {
			this.gameArguments = gameArguments;
			this.conditionalGameArguments = conditionalGameArguments;
			this.jvmArguments = jvmArguments;
			this.conditionaljvmArguments = conditionaljvmArguments;
		}
		
		public ArrayList<String> getGameArguments() {
			return gameArguments;
		}
		
		public ArrayList<ConditionalGameArgument> getConditionalGameArguments() {
			return conditionalGameArguments;
		}
		
		public ArrayList<String> getJvmArguments() {
			return jvmArguments;
		}
		
		public ArrayList<ConditionalJvmArgument> getConditionaljvmArguments() {
			return conditionaljvmArguments;
		}
		
		@Override
		public String toString() {
			return "Arguments [gameArguments=" + gameArguments + ", conditionalGameArguments=" + conditionalGameArguments + ", jvmArguments=" + jvmArguments + ", conditionaljvmArguments=" + conditionaljvmArguments + "]";
		}
		
		public static class ConditionalGameArgument {
			
			private ArrayList<GameRule> rules;
			private Value value;
			
			public ArrayList<GameRule> getRules() {
				return rules;
			}
			
			public Value getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalGameArgument [rules=" + rules + ", value=" + value + "]";
			}
			
			public static class GameRule {
				
				private String action;
				private Features features;
				
				public String getAction() {
					return action;
				}
				
				public Features getFeatures() {
					return features;
				}
				
				@Override
				public String toString() {
					return "GameRule [action=" + action + ", features=" + features + "]";
				}
				
				public static class Features {
					
					private boolean is_demo_user;
					private boolean has_custom_resolution;
					
					public boolean isIsDemoUser() {
						return is_demo_user;
					}
					
					public boolean isHasCustomResolution() {
						return has_custom_resolution;
					}
					
					@Override
					public String toString() {
						return "Features [is_demo_user=" + is_demo_user + ", has_custom_resolution=" + has_custom_resolution + "]";
					}
				}
			}
		}
		
		public static class ConditionalJvmArgument {
			
			private ArrayList<Rule> rules;
			private Value value;
			
			public ArrayList<Rule> getRules() {
				return rules;
			}
			
			public Value getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalArgument [rules=" + rules + ", value=" + value + "]";
			}
		}
		
		public static class Value {
			
			private ArrayList<String> value;
			
			public Value(ArrayList<String> value) {
				this.value = value;
			}
			
			public ArrayList<String> getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "Value [value=" + value + "]";
			}
			
		}
	}
	
	public static class Downloads {
		
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
		
		public static class Client {
			
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
	
	public static class Libary {
		
		private Downloads downloads;
		private Extract extract;
		private String name;
		private Natives natives;
		private ArrayList<Rule> rules;
		private String url;
		
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
			return "Libary [downloads=" + downloads + ", name=" + name + ", url=" + url + ", natives=" + natives + ", extract=" + extract + ", rules=" + rules + "]";
		}
		
		public static class Artifact {
			
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
		
		public static class Classifiers {
			
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
		
		public static class Downloads {
			
			private Artifact artifact;
			private Classifiers classifiers;
			
			public Artifact getArtifact() {
				return artifact;
			}
			
			public Classifiers getClassifiers() {
				return classifiers;
			}
			
			@Override
			public String toString() {
				return "Downloads [artifact=" + artifact + ", classifiers=" + classifiers + "]";
			}
			
		}
		
		public static class Extract {
			
			private ArrayList<String> exclude;
			
			public ArrayList<String> getExclude() {
				return exclude;
			}
			
			@Override
			public String toString() {
				return "Extract [exclude=" + exclude + "]";
			}
		}
		
		public static class Natives {
			
			private String linux;
			private String osx;
			private String windows;
			
			public String getLinux() {
				return linux;
			}
			
			public String getOsx() {
				return osx;
			}
			
			public String getWindows() {
				return windows;
			}
			
			@Override
			public String toString() {
				return "Natives [linux=" + linux + ", osx=" + osx + ", windows=" + windows + "]";
			}
			
		}
		
		public static class Rule {
			
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
			
			public static class OS {
				
				private String name;
				private String version;
				private String arch;
				
				public String getName() {
					return name;
				}
				
				public String getVersion() {
					return version;
				}
				
				public String getArch() {
					return arch;
				}
				
				@Override
				public String toString() {
					return "OS [name=" + name + ", version=" + version + ", arch=" + arch + "]";
				}
			}
		}
	}
}
