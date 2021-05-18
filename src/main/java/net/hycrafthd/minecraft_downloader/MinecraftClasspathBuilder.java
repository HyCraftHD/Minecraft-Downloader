package net.hycrafthd.minecraft_downloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.hycrafthd.minecraft_downloader.library.DownloadableFile;
import net.hycrafthd.minecraft_downloader.settings.GeneratedSettings;
import net.hycrafthd.minecraft_downloader.settings.ProvidedSettings;

public class MinecraftClasspathBuilder {
	
	public static void launch(ProvidedSettings settings) {
		Main.LOGGER.info("Start the classpath builder");
		
		createClassLoader(settings);
		
		Main.LOGGER.info("Finished the classpath builder");
	}
	
	private static void createClassLoader(ProvidedSettings settings) {
		Main.LOGGER.info("Find all jars and create classloader");
		
		final GeneratedSettings generatedSettings = settings.getGeneratedSettings();
		
		final Set<File> classPath = Stream.concat(Stream.of(settings.getClientJarFile()), generatedSettings.getDownloadableFiles() //
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
			} catch (final MalformedURLException ex) {
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
		
		private final ClassLoader ourClassLoader = getClass().getClassLoader();
		
		public MinecraftClassLoader(URL[] urls) {
			super(urls, ClassLoader.getSystemClassLoader());
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.startsWith("net.hycrafthd.minecraft_downloader.auth.api")) {
				return ourClassLoader.loadClass(name);
			}
			return super.loadClass(name);
		}
		
		@Override
		public URL getResource(String name) {
			final URL url = super.getResource(name);
			if (url == null) {
				return ourClassLoader.getResource(name);
			}
			return url;
		}
		
		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			final Enumeration<URL> first = super.getResources(name);
			final Enumeration<URL> second = ourClassLoader.getResources(name);
			
			final Vector<URL> vector = new Vector<>();
			
			while (first.hasMoreElements()) {
				vector.add(first.nextElement());
			}
			
			while (second.hasMoreElements()) {
				vector.add(second.nextElement());
			}
			
			return vector.elements();
		}
	}
}
