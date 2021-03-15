package net.hycrafthd.minecraft_downloader.mojang_api.json_serializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.ArgumentsJson.ValueJson;

public class ValueSerializer implements JsonDeserializer<ValueJson>, JsonSerializer<ValueJson> {
	
	@Override
	public JsonElement serialize(ValueJson value, Type typeOfSrc, JsonSerializationContext context) {
		final ArrayList<String> values = value.getValue();
		
		if (values.size() == 1) {
			return new JsonPrimitive(values.get(0));
		} else {
			final JsonArray json = new JsonArray();
			values.forEach(json::add);
			return json;
		}
	}
	
	@Override
	public ValueJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final ArrayList<String> value = new ArrayList<String>();
		
		if (json.isJsonArray()) {
			json.getAsJsonArray().forEach(element -> value.add(element.getAsString()));
		} else {
			value.add(json.getAsString());
		}
		
		return new ValueJson(value);
	}
	
}
