module net.hycrafthd.minecraft_downloader {
	
	exports net.hycrafthd.minecraft_downloader;
	exports net.hycrafthd.minecraft_downloader.library;
	exports net.hycrafthd.minecraft_downloader.mojang_api;
	exports net.hycrafthd.minecraft_downloader.settings;
	exports net.hycrafthd.minecraft_downloader.util;
	
	requires transitive com.google.gson;
	requires jopt.simple;
	requires net.hycrafthd.minecraft_authenticator;
	requires net.hycrafthd.simple_minecraft_authenticator;
	requires transitive org.apache.logging.log4j;
	requires org.apache.logging.log4j.iostreams;
	requires org.tukaani.xz;
}