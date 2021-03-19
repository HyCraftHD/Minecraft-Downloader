package net.hycrafthd.minecraft_downloader.auth;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.hycrafthd.minecraft_downloader.auth.api.MinecraftAuth;

public class MinecraftAuthImpl implements MinecraftAuth {
	
	private final UserAuthentication auth;
	
	public MinecraftAuthImpl(String username, String password) {
		auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(username);
		auth.setPassword(password);
	}
	
	@Override
	public void logIn() throws IllegalStateException {
		try {
			auth.logIn();
		} catch (AuthenticationException ex) {
			throw new IllegalStateException("Cannot log into account", ex);
		}
	}
	
	@Override
	public String getAuthenticatedToken() {
		return auth.getAuthenticatedToken();
	}
	
	@Override
	public String getUUID() {
		return auth.getSelectedProfile().getId().toString().replace("-", "");
	}
	
	@Override
	public String getName() {
		return auth.getSelectedProfile().getName();
	}
	
	@Override
	public String getUserType() {
		return auth.getUserType().getName();
	}
}
