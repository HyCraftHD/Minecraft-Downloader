package net.hycrafthd.minecraft_downloader.launch;

public class ProvidedArguments {
	
	private String username;
	private String uuid;
	private String accessToken;
	private String userType;
	
	private boolean demo;
	
	private boolean customResolution;
	private int width;
	private int height;
	
	private String gameDir;
	
	public ProvidedArguments(String username, String uuid, String accessToken, String userType, boolean demo, boolean customResolution, int width, int height, String gameDir) {
		this.username = username;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.userType = userType;
		this.demo = demo;
		this.customResolution = customResolution;
		this.width = width;
		this.height = height;
		this.gameDir = gameDir;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getUserType() {
		return userType;
	}
	
	public boolean isDemo() {
		return demo;
	}
	
	public boolean isCustomResolution() {
		return customResolution;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getGameDir() {
		return gameDir;
	}
	
}
