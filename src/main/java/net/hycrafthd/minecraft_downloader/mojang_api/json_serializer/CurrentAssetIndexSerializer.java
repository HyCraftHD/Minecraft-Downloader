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

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentAssetIndexJson.AssetJson;

public class CurrentAssetIndexSerializer implements JsonDeserializer<CurrentAssetIndexJson>, JsonSerializer<CurrentAssetIndexJson> {
	
	@Override
	public JsonElement serialize(CurrentAssetIndexJson assetIndex, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject json = new JsonObject();
		final JsonObject objects = new JsonObject();
		
		assetIndex.getAssets() //
				.entrySet() //
				.stream() //
				.forEach(entry -> {
					objects.add(entry.getKey(), context.serialize(entry.getValue()));
				});
		
		json.add("objects", objects);
		
		if (assetIndex.isMapToResources()) {
			json.addProperty("map_to_resources", assetIndex.isMapToResources());
		}
		
		if (assetIndex.isVirtual()) {
			json.addProperty("virtual", assetIndex.isVirtual());
		}
		
		return json;
	}
	
	@Override
	public CurrentAssetIndexJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		
		final Map<String, AssetJson> assets = object.get("objects") //
				.getAsJsonObject() //
				.entrySet() //
				.stream() //
				.collect(Collectors.toMap(Entry::getKey, entry -> context.deserialize(entry.getValue(), AssetJson.class)));
		
		final boolean mapToResources;
		if (object.has("map_to_resources")) {
			mapToResources = object.get("map_to_resources").getAsBoolean();
		} else {
			mapToResources = false;
		}
		
		final boolean virtual;
		if (object.has("virtual")) {
			virtual = object.get("virtual").getAsBoolean();
		} else {
			virtual = false;
		}
		
		return new CurrentAssetIndexJson(assets, mapToResources, virtual);
	}
	
}
