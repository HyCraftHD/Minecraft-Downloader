package net.hycrafthd.minecraft_downloader;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hycrafthd.minecraft_downloader.util.VersionConstant;

public class Constants {
	
	public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
	public static final String RESOURCES = "https://resources.download.minecraft.net";
	
	public static final String NAME = "Minecraft Downloader";
	public static final String VERSION = "1.0.0";
	
	public static final String URL_SEPERATOR = "/";
	public static final String FILE_SEPERATOR = File.separator;
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static final VersionConstant LIBRARIES = new VersionConstant("libraries");
	public static final VersionConstant NATIVES = new VersionConstant("${version}-natives");
	public static final VersionConstant ASSETS = new VersionConstant("assets");
	
	public static final VersionConstant CLIENT_JSON = new VersionConstant("${version}-client.json");
	public static final VersionConstant CLIENT_JAR = new VersionConstant("${version}-client.jar");
	public static final VersionConstant CLIENT_MAPPINGS = new VersionConstant("${version}-client.txt");
	
}
