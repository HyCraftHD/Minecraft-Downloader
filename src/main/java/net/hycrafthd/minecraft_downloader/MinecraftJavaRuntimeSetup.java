package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.tukaani.xz.LZMAInputStream;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.JavaVersionJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson.FileJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson.FileJson.DownloadsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson.FileJson.DownloadsJson.DownloadJson;
import net.hycrafthd.minecraft_downloader.mojang_api.JavaVersionManifestJson;
import net.hycrafthd.minecraft_downloader.mojang_api.JavaVersionManifestJson.PlatformJson;
import net.hycrafthd.minecraft_downloader.mojang_api.JavaVersionManifestJson.PlatformJson.JavaRuntimeJson;
import net.hycrafthd.minecraft_downloader.mojang_api.JavaVersionManifestJson.PlatformJson.JavaRuntimeJson.ManifestJson;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileDownloadFailedException;
import net.hycrafthd.minecraft_downloader.util.FileUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil;
import net.hycrafthd.minecraft_downloader.util.OSUtil.ARCH;
import net.hycrafthd.minecraft_downloader.util.OSUtil.OS;

public class MinecraftJavaRuntimeSetup {
	
	public static void launch(ProvidedSettings settings, boolean defaultJava, File javaExec) {
		if (defaultJava) {
			downloadJavaRuntime(settings);
		} else if (javaExec != null) {
			settings.getGeneratedSettings().setJavaExec(javaExec);
		} else {
			settings.getGeneratedSettings().setJavaExec(findJavaExecutable(new File(System.getProperty("java.home"))));
		}
	}
	
	private static void downloadJavaRuntime(ProvidedSettings settings) {
		Main.LOGGER.info("Download java runtime");
		
		final JavaRuntimeJson javaRuntimeVersion = extractVersionOfManifest(settings);
		final ManifestJson javaRuntimeVersionManifest = javaRuntimeVersion.getManifest();
		
		final File specificRuntimeDirectory = new File(settings.getRuntimeDirectory(), javaRuntimeVersion.getVersion().getName() + Constants.FILE_SEPERATOR + javaRuntimeVersionManifest.getSha1());
		
		final CurrentJavaVersionJson index;
		
		try {
			final File indexFile = new File(specificRuntimeDirectory, "index.json");
			
			FileUtil.downloadFile(javaRuntimeVersionManifest.getUrl(), indexFile, javaRuntimeVersionManifest.getSize(), javaRuntimeVersionManifest.getSha1());
			
			index = Constants.GSON.fromJson(FileUtil.readText(indexFile), CurrentJavaVersionJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse java runtime index", ex);
		}
		
		final File jreDownloadDirectory = new File(specificRuntimeDirectory, "download");
		FileUtil.createFolders(jreDownloadDirectory);
		
		index.getFiles().entrySet().parallelStream().forEach(entry -> {
			final String name = entry.getKey();
			final FileJson fileObject = entry.getValue();
			
			final String type = fileObject.getType();
			
			if (type.equals("directory")) {
				FileUtil.createFolders(new File(jreDownloadDirectory, name));
			} else if (type.equals("file")) {
				final File file = new File(jreDownloadDirectory, name);
				
				final DownloadsJson downloadsJson = fileObject.getDownloads();
				final DownloadJson lzma = downloadsJson.getLzma();
				final DownloadJson raw = downloadsJson.getRaw();
				
				if (lzma == null) {
					FileUtil.downloadFileException(raw.getUrl(), file, raw.getSize(), raw.getSha1(), "Failed to download raw jre file");
				} else {
					try {
						if (FileUtil.checkFile(file) && FileUtil.checkFileSize(file, raw.getSize()) && FileUtil.checkFileSha1(file, raw.getSha1())) {
							Main.LOGGER.debug("File {} already downloaded and verified", file);
						} else {
							final MessageDigest digest = FileUtil.createSha1Digest();
							
							FileUtil.downloadFile(lzma.getUrl(), file, lzma.getSize(), lzma.getSha1(), inputStream -> new DigestInputStream(new LZMAInputStream(inputStream), digest));
							
							if (!FileUtil.bytesToHex(digest.digest()).equals(raw.getSha1())) {
								throw new IllegalStateException("SHA1 signature does not match the expected one");
							}
							
							if (fileObject.isExecutable()) {
								file.setExecutable(true);
							}
						}
					} catch (final IOException ex) {
						throw new FileDownloadFailedException("Failed to download lzma jre file", lzma.getUrl(), file, ex);
					}
				}
			}
		});
		
		settings.getGeneratedSettings().setJavaExec(findJavaExecutable(jreDownloadDirectory));
		
		Main.LOGGER.info("Finished downloading java runtime");
	}
	
	private static JavaRuntimeJson extractVersionOfManifest(ProvidedSettings settings) {
		Main.LOGGER.info("Download and parse java runtime manifest");
		
		final JavaVersionManifestJson manifest;
		
		try {
			manifest = Constants.GSON.fromJson(FileUtil.downloadText(Constants.JAVA_VERSIONS_MANIFEST), JavaVersionManifestJson.class);
		} catch (final IOException ex) {
			throw new IllegalStateException("Could not download / parse java version manifest json", ex);
		}
		
		final PlatformJson platform = switch (OSUtil.CURRENT_OS) {
		case WINDOWS -> OSUtil.CURRENT_ARCH == ARCH.X86_64 ? manifest.getWindowsX64() : manifest.getWindowsX86();
		case LINUX -> OSUtil.CURRENT_ARCH == ARCH.X86_64 ? manifest.getLinux() : manifest.getLinuxI386();
		case OSX -> OSUtil.CURRENT_ARCH == ARCH.AARCH_64 ? manifest.getMacOsArm64() : manifest.getMacOs();
		default -> null;
		};
		
		if (platform == null) {
			throw new IllegalStateException("Cannot find the right jre for " + OSUtil.CURRENT_OS + " " + OSUtil.CURRENT_ARCH + " (" + OSUtil.CURRENT_VERSION + ")");
		}
		
		final JavaVersionJson javaVersion = settings.getGeneratedSettings().getClientJson().getJavaVersion();
		if (javaVersion != null) {
			final String component = javaVersion.getComponent();
			
			switch (component) {
			case "java-runtime-alpha":
				return platform.getJavaRuntimeAlpha().get(0);
			case "java-runtime-beta":
				return platform.getJavaRuntimeBeta().get(0);
			case "java-runtime-gamma":
				return platform.getJavaRuntimeGamma().get(0);
			default:
				return platform.getJreLegacy().get(0);
			}
		} else {
			return platform.getJreLegacy().get(0);
		}
	}
	
	private static File findJavaExecutable(File jreDirectory) {
		final String javaFile;
		if (OSUtil.CURRENT_OS == OS.WINDOWS) {
			javaFile = Constants.JAVA_EXEC_NAME + ".exe";
		} else {
			javaFile = Constants.JAVA_EXEC_NAME;
		}
		
		return new File(jreDirectory, "bin" + Constants.FILE_SEPERATOR + javaFile);
	}
	
}
