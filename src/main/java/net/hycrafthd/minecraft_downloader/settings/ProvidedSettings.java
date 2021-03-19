package net.hycrafthd.minecraft_downloader.settings;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import net.hycrafthd.minecraft_downloader.Constants;
import net.hycrafthd.minecraft_downloader.util.FileUtil;

public class ProvidedSettings {
	
	private final String version;
	
	private final File outputDirectory;
	
	private final File librariesDirectory;
	private final File nativesDirectory;
	private final File assetsDirectory;
	
	private final File clientJsonFile;
	private final File clientJarFile;
	private final File clientMappingsFile;
	
	private final File authImplFile;
	
	private final Optional<File> runDirectoryOptional;
	
	private final GeneratedSettings generatedSettings;
	
	private final Set<LauncherFeatures> features;
	private final Map<LauncherVariables, String> variables;
	
	public ProvidedSettings(String version, File outputDirectory, File runDirectory) {
		this.version = version;
		this.outputDirectory = outputDirectory;
		
		librariesDirectory = new File(outputDirectory, Constants.LIBRARIES.get(version));
		nativesDirectory = new File(outputDirectory, Constants.NATIVES.get(version));
		assetsDirectory = new File(outputDirectory, Constants.ASSETS.get(version));
		
		clientJsonFile = new File(outputDirectory, Constants.CLIENT_JSON.get(version));
		clientJarFile = new File(outputDirectory, Constants.CLIENT_JAR.get(version));
		clientMappingsFile = new File(outputDirectory, Constants.CLIENT_MAPPINGS.get(version));
		
		authImplFile = new File(outputDirectory, Constants.AUTH_IMPL.get(version));
		
		runDirectoryOptional = Optional.ofNullable(runDirectory);
		
		generatedSettings = new GeneratedSettings();
		features = new HashSet<>();
		variables = new HashMap<>();
	}
	
	public String getVersion() {
		return version;
	}
	
	public File getOutputDirectory() {
		return ensureDirectoryExists(outputDirectory);
	}
	
	public File getLibrariesDirectory() {
		return ensureDirectoryExists(librariesDirectory);
	}
	
	public File getNativesDirectory() {
		return ensureDirectoryExists(nativesDirectory);
	}
	
	public File getAssetsDirectory() {
		return ensureDirectoryExists(assetsDirectory);
	}
	
	public File getClientJsonFile() {
		ensureDirectoryExists(outputDirectory);
		return clientJsonFile;
	}
	
	public File getClientJarFile() {
		ensureDirectoryExists(outputDirectory);
		return clientJarFile;
	}
	
	public File getClientMappingsFile() {
		ensureDirectoryExists(outputDirectory);
		return clientMappingsFile;
	}
	
	public File getAuthImplFile() {
		ensureDirectoryExists(outputDirectory);
		return authImplFile;
	}
	
	public boolean hasRunDirectory() {
		return runDirectoryOptional.isPresent();
	}
	
	public File getRunDirectory() {
		return ensureDirectoryExists(runDirectoryOptional.orElseThrow(() -> new IllegalStateException("Run directory is not set")));
	}
	
	public GeneratedSettings getGeneratedSettings() {
		return generatedSettings;
	}
	
	public void addFeature(LauncherFeatures feature) {
		features.add(feature);
	}
	
	public void addVariable(LauncherVariables variable, File file) {
		addVariable(variable, file.toString());
	}
	
	public void addVariable(LauncherVariables variable, String value) {
		variables.put(variable, value);
	}
	
	public String replaceVariable(String string) {
		String output = string;
		
		for (Entry<LauncherVariables, String> entry : variables.entrySet()) {
			final LauncherVariables variable = entry.getKey();
			final String value = entry.getValue();
			
			output = variable.replaceVariable(output, value);
		}
		
		return output;
	}
	
	private File ensureDirectoryExists(File file) {
		FileUtil.createFolders(file);
		return file;
	}
}
