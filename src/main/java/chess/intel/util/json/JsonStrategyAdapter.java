package chess.intel.util.json;

import chess.intel.strategy.Strategy;
import chess.intel.strategy.NeuralNetwork;

import java.lang.reflect.Type;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;

import com.google.gson.JsonParseException;

public class JsonStrategyAdapter implements JsonDeserializer<Strategy> {
    public Strategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return context.deserialize(jsonObject, NeuralNetwork.class);
    }
}
