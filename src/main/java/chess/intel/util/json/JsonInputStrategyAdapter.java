package chess.intel.util.json;

import chess.intel.training.InputStrategy;
import chess.intel.training.StandardInputStrategy;

import java.lang.reflect.Type;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;

import com.google.gson.JsonParseException;

public class JsonInputStrategyAdapter implements JsonDeserializer<InputStrategy> {
    public InputStrategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return context.deserialize(jsonObject, StandardInputStrategy.class);
    }
}
