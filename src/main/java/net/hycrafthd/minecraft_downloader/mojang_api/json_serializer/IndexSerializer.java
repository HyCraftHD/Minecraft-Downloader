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

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson.AssetJson;

public class IndexSerializer implements JsonDeserializer<CurrentAssetIndexJson>, JsonSerializer<CurrentAssetIndexJson> {
	
	@Override
	public JsonElement serialize(CurrentAssetIndexJson src, Type typeOfSrc, JsonSerializationContext context) {
		return null;
	}
	
	@Override
	public CurrentAssetIndexJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final Map<String, AssetJson> assets = json.getAsJsonObject() //
				.get("objects") //
				.getAsJsonObject() //
				.entrySet() //
				.parallelStream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> context.deserialize(entry.getValue(), AssetJson.class)));
		return new CurrentAssetIndexJson(assets);
	}
	
}
