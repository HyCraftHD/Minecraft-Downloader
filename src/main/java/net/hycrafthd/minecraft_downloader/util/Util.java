package net.hycrafthd.minecraft_downloader.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Util {
	
	public static String downloadJson(String url) throws IOException {
		return downloadJson(url, null);
	}
	
	public static String downloadJson(String url, String sha1) throws IOException {
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.connect();
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}
	
	public static void downloadFile(String url, String sha1) throws IOException {
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.connect();
	}
	
}
