package net.hycrafthd.minecraft_downloader.launch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProvidedSettings {
	
	private final Set<LauncherFeatures> features;
	private final Map<LauncherVariables, String> variables;
	
	public ProvidedSettings() {
		this.features = new HashSet<>();
		this.variables = new HashMap<>();
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
