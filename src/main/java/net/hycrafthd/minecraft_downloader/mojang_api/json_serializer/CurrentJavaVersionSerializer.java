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

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentJavaVersionJson.FileJson;

public class CurrentJavaVersionSerializer implements JsonDeserializer<CurrentJavaVersionJson>, JsonSerializer<CurrentJavaVersionJson> {
	
	@Override
	public JsonElement serialize(CurrentJavaVersionJson javaVersion, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject json = new JsonObject();
		final JsonObject files = new JsonObject();
		
		javaVersion.getFiles() //
				.entrySet() //
				.stream() //
				.forEach(entry -> {
					files.add(entry.getKey(), context.serialize(entry.getValue()));
				});
		
		json.add("files", files);
		
		return json;
	}
	
	@Override
	public CurrentJavaVersionJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		
		final Map<String, FileJson> files = object.get("files") //
				.getAsJsonObject() //
				.entrySet() //
				.stream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> context.deserialize(entry.getValue(), FileJson.class)));
		
		return new CurrentJavaVersionJson(files);
	}
	
}
