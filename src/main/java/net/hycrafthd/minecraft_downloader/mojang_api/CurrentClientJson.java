package net.hycrafthd.minecraft_downloader.mojang_api;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.annotations.JsonAdapter;

import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ArgumentsSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ClassifiersSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.NativesSerializer;
import net.hycrafthd.minecraft_downloader.mojang_api.json_serializer.ValueSerializer;

/**
 * Client version json endpoint <br>
 * See <a href= "https://minecraft.gamepedia.com/Client.json">https://minecraft.gamepedia.com/Client.json</a>
 */
public class CurrentClientJson {
	
	protected ArgumentsJson arguments; // Null in older versions
	protected AssetIndexJson assetIndex;
	protected String assets;
	protected int complianceLevel;
	protected DownloadsJson downloads;
	protected String id;
	protected JavaVersionJson javaVersion;
	protected ArrayList<LibraryJson> libraries;
	protected LoggingJson logging;
	protected String mainClass;
	protected String minecraftArguments; // Null in newer versions
	protected int minimumLauncherVersion;
	protected String releaseTime;
	protected String time;
	protected String type;
	
	public CurrentClientJson(ArgumentsJson arguments, AssetIndexJson assetIndex, String assets, int complianceLevel, DownloadsJson downloads, String id, JavaVersionJson javaVersion, ArrayList<LibraryJson> libraries, LoggingJson logging, String mainClass, String minecraftArguments, int minimumLauncherVersion, String releaseTime, String time, String type) {
		this.arguments = arguments;
		this.assetIndex = assetIndex;
		this.assets = assets;
		this.complianceLevel = complianceLevel;
		this.downloads = downloads;
		this.id = id;
		this.javaVersion = javaVersion;
		this.libraries = libraries;
		this.logging = logging;
		this.mainClass = mainClass;
		this.minecraftArguments = minecraftArguments;
		this.minimumLauncherVersion = minimumLauncherVersion;
		this.releaseTime = releaseTime;
		this.time = time;
		this.type = type;
	}
	
	public ArgumentsJson getArguments() {
		return arguments;
	}
	
	public AssetIndexJson getAssetIndex() {
		return assetIndex;
	}
	
	public String getAssets() {
		return assets;
	}
	
	public int getComplianceLevel() {
		return complianceLevel;
	}
	
	public DownloadsJson getDownloads() {
		return downloads;
	}
	
	public String getId() {
		return id;
	}
	
	public JavaVersionJson getJavaVersion() {
		return javaVersion;
	}
	
	public ArrayList<LibraryJson> getLibraries() {
		return libraries;
	}
	
	public LoggingJson getLogging() {
		return logging;
	}
	
	public String getMainClass() {
		return mainClass;
	}
	
	public String getMinecraftArguments() {
		return minecraftArguments;
	}
	
	public int getMinimumLauncherVersion() {
		return minimumLauncherVersion;
	}
	
	public String getReleaseTime() {
		return releaseTime;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "CurrentClientJson [arguments=" + arguments + ", assetIndex=" + assetIndex + ", assets=" + assets + ", complianceLevel=" + complianceLevel + ", downloads=" + downloads + ", id=" + id + ", javaVersion=" + javaVersion + ", libraries=" + libraries + ", logging=" + logging + ", mainClass=" + mainClass + ", minecraftArguments=" + minecraftArguments + ", minimumLauncherVersion=" + minimumLauncherVersion + ", releaseTime=" + releaseTime + ", time=" + time + ", type=" + type + "]";
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
			
			public static class JvmRuleJson extends BaseOsRuleJson {
				
				public JvmRuleJson(String action, OSJson os) {
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
				return "ValueJson [value=" + value + "]";
			}
			
		}
	}
	
	public static class AssetIndexJson {
		
		protected String id;
		protected String sha1;
		protected int size;
		protected int totalSize;
		protected String url;
		
		public AssetIndexJson(String id, String sha1, int size, int totalSize, String url) {
			this.id = id;
			this.sha1 = sha1;
			this.size = size;
			this.totalSize = totalSize;
			this.url = url;
		}
		
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
			return "AssetIndexJson [id=" + id + ", sha1=" + sha1 + ", size=" + size + ", totalSize=" + totalSize + ", url=" + url + "]";
		}
		
	}
	
	public static class DownloadsJson {
		
		protected ClientJson client;
		protected ClientJson client_mappings;
		protected ClientJson server;
		protected ClientJson server_mappings;
		
		public DownloadsJson(ClientJson client, ClientJson client_mappings, ClientJson server, ClientJson server_mappings) {
			this.client = client;
			this.client_mappings = client_mappings;
			this.server = server;
			this.server_mappings = server_mappings;
		}
		
		public ClientJson getClient() {
			return client;
		}
		
		public ClientJson getClientMappings() {
			return client_mappings;
		}
		
		public ClientJson getServer() {
			return server;
		}
		
		public ClientJson getServerMappings() {
			return server_mappings;
		}
		
		@Override
		public String toString() {
			return "DownloadsJson [client=" + client + ", client_mappings=" + client_mappings + ", server=" + server + ", server_mappings=" + server_mappings + "]";
		}
		
		public static class ClientJson {
			
			protected String sha1;
			protected int size;
			protected String url;
			
			public ClientJson(String sha1, int size, String url) {
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
				return "ClientJson [sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
			}
			
		}
	}
	
	public static class JavaVersionJson {
		
		protected String component;
		protected int majorVersion;
		
		public JavaVersionJson(String component, int majorVersion) {
			this.component = component;
			this.majorVersion = majorVersion;
		}
		
		public String getComponent() {
			return component;
		}
		
		public int getMajorVersion() {
			return majorVersion;
		}
		
		@Override
		public String toString() {
			return "JavaVersionJson [component=" + component + ", majorVersion=" + majorVersion + "]";
		}
		
	}
	
	public static class LibraryJson {
		
		protected DownloadsJson downloads;
		protected String name;
		protected String url;
		protected NativesJson natives;
		protected ExtractJson extract;
		protected ArrayList<LibraryRuleJson> rules;
		
		public DownloadsJson getDownloads() {
			return downloads;
		}
		
		public ExtractJson getExtract() {
			return extract;
		}
		
		public String getName() {
			return name;
		}
		
		public NativesJson getNatives() {
			return natives;
		}
		
		public ArrayList<LibraryRuleJson> getRules() {
			return rules;
		}
		
		public String getUrl() {
			return url;
		}
		
		@Override
		public String toString() {
			return "LibraryJson [downloads=" + downloads + ", name=" + name + ", url=" + url + ", natives=" + natives + ", extract=" + extract + ", rules=" + rules + "]";
		}
		
		public static class DownloadsJson {
			
			protected ArtifactJson artifact;
			protected ClassifiersJson classifiers;
			
			public ArtifactJson getArtifact() {
				return artifact;
			}
			
			public ClassifiersJson getClassifiers() {
				return classifiers;
			}
			
			@Override
			public String toString() {
				return "DownloadsJson [artifact=" + artifact + ", classifiers=" + classifiers + "]";
			}
			
			public static class ArtifactJson {
				
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
					return "ArtifactJson [path=" + path + ", sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
				}
			}
			
			@JsonAdapter(ClassifiersSerializer.class)
			public static class ClassifiersJson {
				
				protected Map<String, ArtifactJson> classifiers;
				
				public ClassifiersJson(Map<String, ArtifactJson> classifiers) {
					this.classifiers = classifiers;
				}
				
				public Map<String, ArtifactJson> getClassifiers() {
					return classifiers;
				}
				
				@Override
				public String toString() {
					return "ClassifiersJson [classifiers=" + classifiers + "]";
				}
			}
		}
		
		@JsonAdapter(NativesSerializer.class)
		public static class NativesJson {
			
			protected Map<String, String> natives;
			
			public NativesJson(Map<String, String> natives) {
				this.natives = natives;
			}
			
			public Map<String, String> getNatives() {
				return natives;
			}
			
			@Override
			public String toString() {
				return "NativesJson [natives=" + natives + "]";
			}
			
		}
		
		public static class ExtractJson {
			
			protected ArrayList<String> exclude;
			
			public ArrayList<String> getExclude() {
				return exclude;
			}
			
			@Override
			public String toString() {
				return "ExtractJson [exclude=" + exclude + "]";
			}
		}
		
		public static class LibraryRuleJson extends BaseOsRuleJson {
			
			public LibraryRuleJson(String action, OSJson os) {
				super(action, os);
			}
			
			@Override
			public String toString() {
				return "LibraryRuleJson [action=" + action + ", os=" + os + "]";
			}
		}
	}
	
	public static class BaseOsRuleJson {
		
		protected String action;
		protected OSJson os;
		
		public BaseOsRuleJson(String action, OSJson os) {
			this.action = action;
			this.os = os;
		}
		
		public String getAction() {
			return action;
		}
		
		public OSJson getOs() {
			return os;
		}
		
		@Override
		public String toString() {
			return "BaseOsRuleJson [action=" + action + ", os=" + os + "]";
		}
		
		public static class OSJson {
			
			protected String name;
			protected String version;
			protected String arch;
			
			public OSJson(String name, String version, String arch) {
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
	
	public static class LoggingJson {
		
		protected LoggingClientJson client;
		
		public LoggingJson(LoggingClientJson client) {
			this.client = client;
		}
		
		public LoggingClientJson getClient() {
			return client;
		}
		
		@Override
		public String toString() {
			return "LoggingJson [client=" + client + "]";
		}
		
		public static class LoggingClientJson {
			
			protected String argument;
			protected LoggingFileJson file;
			protected String type;
			
			public LoggingClientJson(String argument, LoggingFileJson file, String type) {
				this.argument = argument;
				this.file = file;
				this.type = type;
			}
			
			public String getArgument() {
				return argument;
			}
			
			public LoggingFileJson getFile() {
				return file;
			}
			
			public String getType() {
				return type;
			}
			
			@Override
			public String toString() {
				return "LoggingClientJson [argument=" + argument + ", file=" + file + ", type=" + type + "]";
			}
			
			public static class LoggingFileJson {
				
				protected String id;
				protected String sha1;
				protected int size;
				protected String url;
				
				public LoggingFileJson(String id, String sha1, int size, String url) {
					this.id = id;
					this.sha1 = sha1;
					this.size = size;
					this.url = url;
				}
				
				public String getId() {
					return id;
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
					return "LoggingFileJson [id=" + id + ", sha1=" + sha1 + ", size=" + size + ", url=" + url + "]";
				}
			}
		}
	}
}
