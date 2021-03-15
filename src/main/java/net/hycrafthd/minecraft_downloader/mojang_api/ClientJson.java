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
	
	protected ArgumentsJson arguments;
	protected AssetIndex assetIndex;
	protected String assets;
	protected Downloads downloads;
	protected ArrayList<Library> libraries;
	
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
		
		protected final ArrayList<String> gameArguments;
		protected final ArrayList<ConditionalGameArgumentJson> conditionalGameArguments;
		
		protected final ArrayList<String> jvmArguments;
		protected final ArrayList<ConditionalJvmArgumentJson> conditionalJvmArguments;
		
		public ArgumentsJson(ArrayList<String> gameArguments, ArrayList<ConditionalGameArgumentJson> conditionalGameArguments, ArrayList<String> jvmArguments, ArrayList<ConditionalJvmArgumentJson> conditionaljvmArguments) {
			this.gameArguments = gameArguments;
			this.conditionalGameArguments = conditionalGameArguments;
			this.jvmArguments = jvmArguments;
			this.conditionalJvmArguments = conditionaljvmArguments;
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
		
		public ArrayList<ConditionalJvmArgumentJson> getConditionalJvmArguments() {
			return conditionalJvmArguments;
		}
		
		@Override
		public String toString() {
			return "ArgumentsJson [gameArguments=" + gameArguments + ", conditionalGameArguments=" + conditionalGameArguments + ", jvmArguments=" + jvmArguments + ", conditionalJvmArguments=" + conditionalJvmArguments + "]";
		}
		
		public static class ConditionalGameArgumentJson {
			
			protected ArrayList<GameRuleJson> rules;
			protected ValueJson value;
			
			public ConditionalGameArgumentJson(ArrayList<GameRuleJson> rules, ValueJson value) {
				this.rules = rules;
				this.value = value;
			}
			
			public ArrayList<GameRuleJson> getRules() {
				return rules;
			}
			
			public ValueJson getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalGameArgumentJson [rules=" + rules + ", value=" + value + "]";
			}
			
			public static class GameRuleJson {
				
				protected String action;
				protected FeaturesJson features;
				
				public GameRuleJson(String action, FeaturesJson features) {
					this.action = action;
					this.features = features;
				}
				
				public String getAction() {
					return action;
				}
				
				public FeaturesJson getFeatures() {
					return features;
				}
				
				@Override
				public String toString() {
					return "GameRuleJson [action=" + action + ", features=" + features + "]";
				}
				
				public static class FeaturesJson {
					
					protected boolean is_demo_user;
					protected boolean has_custom_resolution;
					
					public FeaturesJson(boolean is_demo_user, boolean has_custom_resolution) {
						this.is_demo_user = is_demo_user;
						this.has_custom_resolution = has_custom_resolution;
					}
					
					public boolean isIsDemoUser() {
						return is_demo_user;
					}
					
					public boolean isHasCustomResolution() {
						return has_custom_resolution;
					}
					
					@Override
					public String toString() {
						return "FeaturesJson [is_demo_user=" + is_demo_user + ", has_custom_resolution=" + has_custom_resolution + "]";
					}
					
				}
			}
		}
		
		public static class ConditionalJvmArgumentJson {
			
			protected ArrayList<JvmRuleJson> rules;
			protected ValueJson value;
			
			public ConditionalJvmArgumentJson(ArrayList<JvmRuleJson> rules, ValueJson value) {
				this.rules = rules;
				this.value = value;
			}
			
			public ArrayList<JvmRuleJson> getRules() {
				return rules;
			}
			
			public ValueJson getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "ConditionalJvmArgumentJson [rules=" + rules + ", value=" + value + "]";
			}
			
			public static class JvmRuleJson extends BaseOsRule {
				
				public JvmRuleJson(String action, OS os) {
					super(action, os);
				}
				
				@Override
				public String toString() {
					return "JvmRuleJson [action=" + action + ", os=" + os + "]";
				}
			}
			
		}
		
		@JsonAdapter(ValueSerializer.class)
		public static class ValueJson {
			
			protected final ArrayList<String> value;
			
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
		
		protected String id;
		protected String sha1;
		protected int size;
		protected int totalSize;
		protected String url;
		
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
		
		protected Client client;
		protected Client client_mappings;
		protected Client server;
		protected Client server_mappings;
		
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
			
			protected String sha1;
			protected int size;
			
			protected String url;
			
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
		
		protected Downloads downloads;
		protected Extract extract;
		protected String name;
		protected Natives natives;
		protected ArrayList<BaseOsRule> rules;
		protected String url;
		
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
		
		public ArrayList<BaseOsRule> getRules() {
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
			
			protected String path;
			protected String sha1;
			protected int size;
			protected String url;
			
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
			
			protected Artifact javadoc;
			@SerializedName(value = "natives-linux")
			protected Artifact natives_linux;
			@SerializedName(value = "natives-macos")
			protected Artifact natives_macos;
			@SerializedName(value = "natives-windows")
			protected Artifact natives_windows;
			protected Artifact sources;
			
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
			
			protected Artifact artifact;
			protected Classifiers classifiers;
			
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
			
			protected ArrayList<String> exclude;
			
			public ArrayList<String> getExclude() {
				return exclude;
			}
			
			@Override
			public String toString() {
				return "Extract [exclude=" + exclude + "]";
			}
		}
		
		public static class Natives {
			
			protected String linux;
			protected String osx;
			protected String windows;
			
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
	
	public static class BaseOsRule {
		
		protected String action;
		protected OS os;
		
		public BaseOsRule(String action, OS os) {
			this.action = action;
			this.os = os;
		}
		
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
			
			protected String name;
			protected String version;
			protected String arch;
			
			public OS(String name, String version, String arch) {
				this.name = name;
				this.version = version;
				this.arch = arch;
			}
			
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
