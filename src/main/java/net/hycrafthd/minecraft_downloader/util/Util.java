package net.hycrafthd.minecraft_downloader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

public class Util {
	
	private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
	
	public static String downloadJson(String url) throws IOException {
		return downloadJson(url, null);
	}
	
	public static String downloadJson(String url, String sha1) throws IOException {
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		urlConnection.connect();
		
		final MessageDigest messageDigest;
		
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException ex) {
			throw new SHA1VerificationFailedException("SHA1 Algorithm not available");
		}
		
		final String json;
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new DigestInputStream(urlConnection.getInputStream(), messageDigest), StandardCharsets.UTF_8))) {
			json = reader.lines().collect(Collectors.joining("\n"));
		}
		
		if (sha1 != null) {
			if (!bytesToHex(messageDigest.digest()).equals(sha1)) {
				throw new SHA1VerificationFailedException("SHA1 signature does not match the expected one");
			}
		}
		
		return json;
	}
	
	public static void downloadFile(String url, String sha1, File output) throws IOException {
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.connect();
		
		final MessageDigest messageDigest;
		
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException ex) {
			throw new SHA1VerificationFailedException("SHA1 Algorithm not available");
		}
		
		final byte buffer[] = new byte[2048];
		
		try (final InputStream inputStream = new DigestInputStream(urlConnection.getInputStream(), messageDigest); //
				final OutputStream outputStream = new FileOutputStream(output)) {
			int count;
			while ((count = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, count);
			}
		}
		
		if (sha1 != null) {
			if (!bytesToHex(messageDigest.digest()).equals(sha1)) {
				throw new SHA1VerificationFailedException("SHA1 signature does not match the expected one");
			}
		}
	}
	
	public static String bytesToHex(byte[] bytes) {
		byte[] hexChars = new byte[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars, StandardCharsets.UTF_8);
	}
	
}
