package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class MinecraftClasspathBuilder {
	
	static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start the classpath builder");
		
		extractAuthImpl(settings);
		createClassLoader(settings);
		
		Main.LOGGER.info("Finished the classpath builder");
	}
	
	private static void extractAuthImpl(ProvidedSettings settings) {
		Main.LOGGER.info("Extract auth lib");
		
		final File file = settings.getAuthImplFile();
		
		try (final InputStream inputStream = MinecraftClasspathBuilder.class.getResourceAsStream("/" + Constants.AUTH_IMPL_JAR); //
				final OutputStream outputStream = new FileOutputStream(file)) {
			FileUtil.copy(inputStream, outputStream, new byte[2048]);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not extract minecraft auth impl to " + file, ex);
		}
	}
	
	private static void createClassLoader(ProvidedSettings settings) {
		Main.LOGGER.info("Find all jars and create classloader");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final Set<File> classPath = Stream.concat(Stream.of(settings.getClientJarFile(), settings.getAuthImplFile()), generatedSettings.getDownloadableFiles() //
				.stream() //
				.filter(downloadableFile -> !downloadableFile.isNative()) //
				.filter(DownloadableFile::hasDownloadedFile) //
				.map(DownloadableFile::getDownloadedFile)) //
				.collect(Collectors.toSet());
		
		Main.LOGGER.debug("The classpath entries are: ");
		classPath.forEach(file -> {
			Main.LOGGER.debug(" " + file);
		});
		
		final MinecraftClassLoader classLoader = new MinecraftClassLoader(classPath.stream().map(file -> {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException ex) {
				throw new IllegalStateException("Cannot get url from file " + file, ex);
			}
		}).toArray(URL[]::new));
		
		generatedSettings.setClassPath(classPath);
		generatedSettings.setClassLoader(classLoader);
	}
	
	private static class MinecraftClassLoader extends URLClassLoader {
		
		static {
			ClassLoader.registerAsParallelCapable();
		}
		
		public MinecraftClassLoader(URL[] urls) {
			super(urls, ClassLoader.getSystemClassLoader());
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.startsWith("net.hycrafthd.minecraft_downloader.auth.api")) {
				return ClassLoader.getSystemClassLoader().loadClass(name);
			}
			return super.loadClass(name);
		}
	}
}
