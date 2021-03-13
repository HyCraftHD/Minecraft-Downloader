package net.hycrafthd.minecraft_downloader.util;

import java.io.IOException;

public class SHA1VerificationFailedException extends IOException {
	
	private static final long serialVersionUID = 1L;
	
	public SHA1VerificationFailedException() {
	}
	
	public SHA1VerificationFailedException(String string) {
		super(string);
	}
	
}
