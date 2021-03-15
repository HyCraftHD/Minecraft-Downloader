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

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.LibraryJson.NativesJson;

public class NativesSerializer implements JsonDeserializer<NativesJson>, JsonSerializer<NativesJson> {
	
	@Override
	public JsonElement serialize(NativesJson natives, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject json = new JsonObject();
		
		natives.getNatives() //
				.entrySet() //
				.stream() //
				.forEach(entry -> {
					json.addProperty(entry.getKey(), entry.getValue());
				});
		
		return json;
	}
	
	@Override
	public NativesJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final Map<String, String> natives = json.getAsJsonObject() //
				.entrySet() //
				.stream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getAsString()));
		return new NativesJson(natives);
	}
	
}
