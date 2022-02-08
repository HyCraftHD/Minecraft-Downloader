package net.hycrafthd.minecraft_downloader.settings;

import net.hycrafthd.minecraft_downloader.util.StringUtil;

public enum LauncherVariables {
	
	AUTH_PLAYER_NAME("auth_player_name"),
	AUTH_UUID("auth_uuid"),
	AUTH_ACCESS_TOKEN("auth_access_token"),
	AUTH_XUID("auth_xuid"),
	AUTH_SESSION("auth_session"),
	USER_TYPE("user_type"),
	USER_PROPERTIES("user_properties"),
	USER_PROPERTY_MAP("user_property_map"),
	CLIENT_ID("clientid"),
	
	VERSION_NAME("version_name"),
	VERSION_TYPE("version_type"),
	
	GAME_DIRECTORY("game_directory"),
	GAME_ASSETS("game_assets"),
	
	ASSET_ROOT("assets_root"),
	ASSET_INDEX_NAME("assets_index_name"),
	
	RESOLUTION_WIDTH("resolution_width"),
	RESOLUTION_HEIGHT("resolution_height"),
	
	LAUNCHER_NAME("launcher_name"),
	LAUNCHER_VERSION("launcher_version"),
	
	NATIVE_DIRECTORY("natives_directory"),
	CLASSPATH("classpath");
	
	private final String name;
	
	private LauncherVariables(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String replaceVariable(String string, String value) {
		return StringUtil.replaceVariable(name, string, value);
	}
}
