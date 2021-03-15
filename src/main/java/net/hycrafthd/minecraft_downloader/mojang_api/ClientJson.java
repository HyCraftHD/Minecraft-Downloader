package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.ArrayList;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ArgumentsSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ValueSerializer;

/**
 * Client version json endpoint <br>
 * See <a href= "https://minecraft.gamepedia.com/Client.json">https://minecraft.gamepedia.com/Client.json</a>
 */
public class ClientJson {
	
	private ArgumentsJson arguments;
	private AssetIndex assetIndex;
	private String assets;
	private Downloads downloads;
	private ArrayList<Library> libraries;
	
	public ArgumentsJson getArguments() {
		return arguments;
	}
	
	public AssetIndex getAssetIndex() {
		return assetIndex;
	}
	
	public String getAssets() {
		return assets;
	}
	
	public Downloads getDownloads() {
		return downloads;
	}
	
	public ArrayList<Library> getLibraries() {
		return libraries;
	}
	
	@Override
	public String toString() {
		return "ClientJson [arguments=" + arguments + ", assetIndex=" + assetIndex + ", assets=" + assets + ", downloads=" + downloads + ", libraries=" + libraries + "]";
	}
	
	@JsonAdapter(ArgumentsSerializer.class)
	public static class ArgumentsJson {
		
		private final ArrayList<String> gameArguments;
		private final ArrayList<ConditionalGameArgumentJson> conditionalGameArguments;
		
		private final ArrayList<String> jvmArguments;
		private final ArrayList<ConditionalJvmArgumentJson> conditionaljvmArguments;
		
		public ArgumentsJson(ArrayList<String> gameArguments, ArrayList<ConditionalGameArgumentJson> conditionalGameArguments, ArrayList<String> jvmArguments, ArrayList<ConditionalJvmArgumentJson> conditionaljvmArguments) {
			this.gameArguments = gameArguments;
			this.conditionalGameArguments = conditionalGameArguments;
			this.jvmArguments = jvmArguments;
			this.conditionaljvmArguments = conditionaljvmArguments;
		}
		
		public ArrayList<String> getGameArguments() {
			return gameArguments;
		}
		
		public ArrayList<ConditionalGameArgumentJson> getConditionalGameArguments() {
			return conditionalGameArguments;
		}
		
		public ArrayList<String> getJvmArguments() {
			return jvmArguments;
		}
		
		public ArrayList<ConditionalJvmArgumentJson> getConditionaljvmArguments() {
			return conditionaljvmArguments;
		}
		
		@Override
		public String toString() {
			return "Arguments [gameArguments=" + gameArguments + ", conditionalGameArguments=" + conditionalGameArguments + ", jvmArguments=" + jvmArguments + ", conditionaljvmArguments=" + conditionaljvmArguments + "]";
		}
		
		public static class ConditionalGameArgumentJson {
			
			private ArrayList<GameRuleJson> rules;
			private ValueJson value;
			
			public ArrayList<GameRuleJson> getRules() {
				return rules;
			}
			
			public ValueJson getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalGameArgument [rules=" + rules + ", value=" + value + "]";
			}
			
			public static class GameRuleJson {
				
				private String action;
				private FeaturesJson features;
				
				public String getAction() {
					return action;
				}
				
				public FeaturesJson getFeatures() {
					return features;
				}
				
				@Override
				public String toString() {
					return "GameRule [action=" + action + ", features=" + features + "]";
				}
				
				public static class FeaturesJson {
					
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
		
		public static class ConditionalJvmArgumentJson {
			
			private ArrayList<JvmRuleJson> rules;
			private ValueJson value;
			
			public ArrayList<JvmRuleJson> getRules() {
				return rules;
			}
			
			public ValueJson getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalArgument [rules=" + rules + ", value=" + value + "]";
			}
			
			public static class JvmRuleJson extends Rule {
			}
			
		}
		
		@JsonAdapter(ValueSerializer.class)
		public static class ValueJson {
			
			private final ArrayList<String> value;
			
			public ValueJson(ArrayList<String> value) {
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
	
	public static class AssetIndex {
		
		private String id;
		private String sha1;
		private int size;
		private int totalSize;
		private String url;
		
		public String getId() {
			return id;
		}
		
		public String getSha1() {
			return sha1;
		}
		
		public int getSize() {
			return size;
		}
		
		public int getTotalSize() {
			return totalSize;
		}
		
		public String getUrl() {
			return url;
		}
		
		@Override
		public String toString() {
			return "AssetIndex [id=" + id + ", sha1=" + sha1 + ", size=" + size + ", totalSize=" + totalSize + ", url=" + url + "]";
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
	
	public static class Library {
		
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
