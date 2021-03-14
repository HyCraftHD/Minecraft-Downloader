package net.hycrafthd.minecraft_downloader.mojang_api.json_serializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_downloader.mojang_api.Index;
import net.hycrafthd.minecraft_downloader.mojang_api.Index.AssetObject;

public class IndexSerializer implements JsonDeserializer<Index>, JsonSerializer<Index> {
	
	@Override
	public JsonElement serialize(Index src, Type typeOfSrc, JsonSerializationContext context) {
		return null;
	}
	
	@Override
	public Index deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final Map<String, AssetObject> assets = json.getAsJsonObject() //
				.get("objects") //
				.getAsJsonObject() //
				.entrySet() //
				.parallelStream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> context.deserialize(entry.getValue(), AssetObject.class)));
		return new Index(assets);
	}
	
}
