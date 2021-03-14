package net.hycrafthd.minecraft_downloader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

import net.hycrafthd.minecraft_downloader.Main;

public class Util {
	
	private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
	
	public static String downloadText(String url) throws IOException {
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		urlConnection.connect();
		
		return readText(urlConnection.getInputStream());
	}
	
	public static void downloadFileException(String url, File output, int expectedSize, String expectedSha1, String exception) {
		try {
			downloadFile(url, output, expectedSize, expectedSha1);
		} catch (IOException ex) {
			throw new FileDownloadFailedException(exception, url, output, ex);
		}
	}
	
	public static void downloadFile(String url, File output, String expectedSha1) throws IOException {
		downloadFile(url, output, null, expectedSha1);
	}
	
	public static void downloadFile(String url, File output, Integer expectedSize, String expectedSha1) throws IOException {
		Main.LOGGER.debug("Try to download file from {} to {}", url, output);
		
		final MessageDigest digest = createSha1Digest();
		
		if (checkFile(output)) {
			final boolean sizeCheck;
			if (expectedSize != null) {
				sizeCheck = checkFileSize(output, expectedSize);
			} else {
				sizeCheck = true;
			}
			
			if (sizeCheck && expectedSha1 != null && checkFileSha1(digest, output, expectedSha1)) {
				Main.LOGGER.debug("File {} already downloaded and verified", output);
				return;
			}
		}
		
		final URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		urlConnection.connect();
		
		createParentFolders(output);
		
		final byte buffer[] = new byte[8192];
		
		try (final InputStream inputStream = new DigestInputStream(urlConnection.getInputStream(), digest); //
				final OutputStream outputStream = new FileOutputStream(output)) {
			copy(inputStream, outputStream, buffer);
		}
		
		if (expectedSha1 != null) {
			if (!bytesToHex(digest.digest()).equals(expectedSha1)) {
				throw new IllegalStateException("SHA1 signature does not match the expected one");
			}
		}
		
		Main.LOGGER.debug("Finished to download file {}", output);
	}
	
	public static boolean checkFile(File file) {
		return file.exists() && file.isFile() && file.canRead() && file.canWrite();
	}
	
	public static boolean checkFileSize(File file, int expectedSize) {
		return file.length() == expectedSize;
	}
	
	public static boolean checkFileSha1(MessageDigest digest, File file, String expectedSha1) throws IOException {
		final byte buffer[] = new byte[8192];
		
		try (final InputStream inputStream = new FileInputStream(file)) {
			int count;
			while ((count = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, count);
			}
		}
		
		return bytesToHex(digest.digest()).equals(expectedSha1);
	}
	
	public static String readText(File file) throws IOException {
		try (final FileInputStream fileInputStream = new FileInputStream(file)) {
			return readText(fileInputStream);
		}
	}
	
	public static String readText(InputStream inputStream) throws IOException {
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}
	
	public static void createParentFolders(File file) {
		final File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
	}
	
	public static void copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
		int count;
		while ((count = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, count);
		}
	}
	
	public static MessageDigest createSha1Digest() {
		try {
			return MessageDigest.getInstance("SHA1");
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA1 Algorithm not available");
		}
	}
	
	public static String bytesToHex(byte[] bytes) {
		final byte[] hexChars = new byte[bytes.length * 2];
		for (int index = 0; index < bytes.length; index++) {
			final int byteAtIndex = bytes[index] & 0xFF;
			hexChars[index * 2] = HEX_ARRAY[byteAtIndex >>> 4];
			hexChars[index * 2 + 1] = HEX_ARRAY[byteAtIndex & 0x0F];
		}
		return new String(hexChars, StandardCharsets.UTF_8);
	}
	
}
