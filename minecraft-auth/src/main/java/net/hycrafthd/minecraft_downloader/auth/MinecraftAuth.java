package net.hycrafthd.minecraft_downloader.auth;

public interface MinecraftAuth {
	
	void logIn() throws IllegalStateException;
	
	String getAuthenticatedToken();
	
	String getUUID();
	
	String getName();
	
	String getUserType();
}
