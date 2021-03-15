package net.hycrafthd.minecraft_downloader.mojang_api.json_serializer;

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

import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalGameArgumentJson;
import net.hycrafthd.minecraft_downloader.mojang_api.CurrentClientJson.ArgumentsJson.ConditionalJvmArgumentJson;

public class ArgumentsSerializer implements JsonDeserializer<ArgumentsJson>, JsonSerializer<ArgumentsJson> {
	
	@Override
	public JsonElement serialize(ArgumentsJson arguments, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject json = new JsonObject();
		final JsonArray gameArray = new JsonArray();
		
		arguments.getGameArguments().forEach(argument -> gameArray.add(argument));
		arguments.getConditionalGameArguments().forEach(conditionalArgument -> gameArray.add(context.serialize(conditionalArgument)));
		
		json.add("game", gameArray);
		
		final JsonArray jvmArray = new JsonArray();
		
		arguments.getConditionalJvmArguments().forEach(conditionalArgument -> jvmArray.add(context.serialize(conditionalArgument)));
		arguments.getJvmArguments().forEach(argument -> jvmArray.add(argument));
		
		json.add("jvm", jvmArray);
		
		return json;
	}
	
	@Override
	public ArgumentsJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final JsonArray gameArray = object.get("game").getAsJsonArray();
		
		final ArrayList<String> gameArguments = new ArrayList<>();
		final ArrayList<ConditionalGameArgumentJson> conditionalGameArguments = new ArrayList<>();
		
		gameArray.forEach(element -> {
			if (element.isJsonPrimitive()) {
				gameArguments.add(element.getAsString());
			} else {
				conditionalGameArguments.add(context.deserialize(element.getAsJsonObject(), ConditionalGameArgumentJson.class));
			}
		});
		
		final JsonArray jvmArray = object.get("jvm").getAsJsonArray();
		
		final ArrayList<String> jvmArguments = new ArrayList<>();
		final ArrayList<ConditionalJvmArgumentJson> conditionaljvmArguments = new ArrayList<>();
		
		jvmArray.forEach(element -> {
			if (element.isJsonPrimitive()) {
				jvmArguments.add(element.getAsString());
			} else {
				conditionaljvmArguments.add(context.deserialize(element, ConditionalJvmArgumentJson.class));
			}
		});
		
		return new ArgumentsJson(gameArguments, conditionalGameArguments, jvmArguments, conditionaljvmArguments);
	}
	
}
