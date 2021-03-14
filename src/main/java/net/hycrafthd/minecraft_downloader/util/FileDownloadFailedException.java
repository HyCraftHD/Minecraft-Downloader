package net.hycrafthd.minecraft_downloader.util;

import java.io.File;

public class FileDownloadFailedException extends IllegalStateException {
	
	private static final long serialVersionUID = 1L;
	
	public FileDownloadFailedException(String message, String url, File file, Throwable cause) {
		super(message + " (Download from URL: " + url + " to file " + file.getAbsolutePath() + ")", cause);
	}
	
}
