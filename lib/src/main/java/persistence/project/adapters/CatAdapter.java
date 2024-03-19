package persistence.project.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import persistence.project.examples.Cat;

public class CatAdapter implements JsonSerializer<Cat>, JsonDeserializer<Cat> {

  @Override
  public Cat deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    return null;
  }

  @Override
  public JsonElement serialize(Cat cat, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("newField", "newValue");
    return jsonObject;
  }
}
