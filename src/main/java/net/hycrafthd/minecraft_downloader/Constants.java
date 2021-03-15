package net.hycrafthd.minecraft_downloader;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	public static final String RESOURCES = "https://resources.download.minecraft.net";
	
	public static final String FILE_SEPERATOR = File.separator;
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static final String CLIENT_JSON = "client.json";
	public static final String CLIENT_JAR = "client.jar";
	public static final String CLIENT_MAPPINGS = "client.txt";
	
	public static final String LIBRARIES = "libraries";
	public static final String NATIVES = "natives";
	public static final String ASSETS = "assets";
}
