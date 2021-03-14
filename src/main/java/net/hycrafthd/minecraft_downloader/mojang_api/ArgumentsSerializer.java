package net.hycrafthd.minecraft_downloader.mojang_api;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments.ConditionalGameArgument;
import net.hycrafthd.minecraft_downloader.mojang_api.ClientJson.Arguments.ConditionalJvmArgument;

public class ArgumentsSerializer implements JsonDeserializer<Arguments>, JsonSerializer<Arguments> {
	
	@Override
	public JsonElement serialize(Arguments src, Type typeOfSrc, JsonSerializationContext context) {
		// TODO
		return null;
	}
	
	@Override
	public Arguments deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final JsonArray gameArray = object.get("game").getAsJsonArray();
		
		final ArrayList<String> gameArguments = new ArrayList<>();
		final ArrayList<ConditionalGameArgument> conditionalGameArguments = new ArrayList<>();
		
		gameArray.forEach(element -> {
			if (element.isJsonPrimitive()) {
				gameArguments.add(element.getAsString());
			} else {
				conditionalGameArguments.add(context.deserialize(element.getAsJsonObject(), ConditionalGameArgument.class));
			}
		});
		
		final JsonArray jvmArray = object.get("jvm").getAsJsonArray();
		
		final ArrayList<String> jvmArguments = new ArrayList<>();
		final ArrayList<ConditionalJvmArgument> conditionaljvmArguments = new ArrayList<>();
		
		jvmArray.forEach(element -> {
			if (element.isJsonPrimitive()) {
				jvmArguments.add(element.getAsString());
			} else {
				conditionaljvmArguments.add(context.deserialize(element, ConditionalJvmArgument.class));
			}
		});
		
		return new Arguments(gameArguments, conditionalGameArguments, jvmArguments, conditionaljvmArguments);
	}
	
}
