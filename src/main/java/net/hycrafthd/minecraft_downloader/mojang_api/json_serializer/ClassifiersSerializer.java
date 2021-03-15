package net.hycrafthd.minecraft_downloader.mojang_api.json_serializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.DownloadsJson.ArtifactJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.DownloadsJson.ClassifiersJson;

public class ClassifiersSerializer implements JsonDeserializer<ClassifiersJson>, JsonSerializer<ClassifiersJson> {
	
	@Override
	public JsonElement serialize(ClassifiersJson classifiers, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject json = new JsonObject();
		
		classifiers.getClassifiers() //
				.entrySet() //
				.stream() //
				.forEach(entry -> {
					json.add(entry.getKey(), context.serialize(entry.getValue()));
				});
		
		return json;
	}
	
	@Override
	public ClassifiersJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final Map<String, ArtifactJson> classifiers = json.getAsJsonObject() //
				.entrySet() //
				.stream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> context.deserialize(entry.getValue(), ArtifactJson.class)));
		return new ClassifiersJson(classifiers);
	}
	
}
