package net.hycrafthd.minecraft_downloader.launch;

public enum LauncherVariables {
	
	AUTH_PLAYER_NAME("auth_player_name"),
	AUTH_UUID("auth_uuid"),
	AUTH_ACCESS_TOKEN("auth_access_token"),
	USER_TYPE("user_type"),
	
	VERSION_NAME("version_name"),
	VERSION_TYPE("version_type"),
	
	GAME_DIRECTORY("game_directory"),
	
	ASSET_ROOT("assets_root"),
	ASSET_INDEX_NAME("assets_index_name"),
	
	RESOLUTION_WIDTH("resolution_width"),
	RESOLUTION_HEIGHT("resolution_height"),
	
	NATIVE_DIRECTORY("natives_directory"),
	LAUNCHER_NAME("launcher_name"),
	LAUNCHER_VERSION("launcher_version"),
	CLASSPATH("classpath");
	
	private final String name;
	private final String replace;
	
	private LauncherVariables(String name) {
		this.name = name;
		replace = "{" + name + "}";
	}
	
	public String getName() {
		return name;
	}
	
	public String getReplace() {
		return replace;
	}
	
	public String replaceVariable(String string, String value) {
		return string.replace(replace, value);
	}
}
