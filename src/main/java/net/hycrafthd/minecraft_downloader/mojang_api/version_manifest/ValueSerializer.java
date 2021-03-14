package net.hycrafthd.minecraft_downloader.mojang_api.version_manifest;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_downloader.mojang_api.version_manifest.ClientJson.Arguments.Value;

public class ValueSerializer implements JsonDeserializer<Value>, JsonSerializer<Value> {
	
	@Override
	public JsonElement serialize(Value src, Type typeOfSrc, JsonSerializationContext context) {
		// TODO
		return null;
	}
	
	@Override
	public Value deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final ArrayList<String> value = new ArrayList<String>();
		
		if (json.isJsonArray()) {
			json.getAsJsonArray().forEach(element -> value.add(element.getAsString()));
		} else {
			value.add(json.getAsString());
		}
		
		return new Value(value);
	}
	
}
