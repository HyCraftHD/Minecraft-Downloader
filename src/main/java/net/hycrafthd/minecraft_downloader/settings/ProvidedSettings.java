package net.hycrafthd.minecraft_downloader.settings;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProvidedSettings {
	
	private final String version;
	private final File output;
	
	private final Set<LauncherFeatures> features;
	private final Map<LauncherVariables, String> variables;
	
	public ProvidedSettings(String version, File output) {
		this.version = version;
		this.output = output;
		this.features = new HashSet<>();
		this.variables = new HashMap<>();
	}
	
	public String getVersion() {
		return version;
	}
	
	public File getOutput() {
		return output;
	}
	
	public void addFeature(LauncherFeatures feature) {
		features.add(feature);
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
	
}
