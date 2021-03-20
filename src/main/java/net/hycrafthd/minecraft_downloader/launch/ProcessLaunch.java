package net.hycrafthd.minecraft_downloader.launch;

import java.io.File;
import java.io.IOException;

import net.hycrafthd.minecraft_downloader.Constants;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil.OS;

public class ProcessLaunch {
	
	public static void launch(ProvidedSettings settings, File javaExec) {
		
		System.out.println(findJavaExecutable(javaExec));
		
		final ProcessBuilder processBuilder = new ProcessBuilder("java", "-version");
		
		processBuilder.inheritIO();
		
		try {
			Process process = processBuilder.start();
			System.out.println(process.waitFor());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static String findJavaExecutable(File javaExec) {
		if (javaExec == null || !javaExec.canRead()) {
			final String javaFile;
			if (OSUtil.CURRENT_OS == OS.WINDOWS) {
				javaFile = "java.exe";
			} else {
				javaFile = "java";
			}
			
			return new File(System.getProperty("java.home"), "bin" + Constants.FILE_SEPERATOR + javaFile).getAbsolutePath();
		} else {
			return javaExec.getAbsolutePath();
		}
	}
	
}
