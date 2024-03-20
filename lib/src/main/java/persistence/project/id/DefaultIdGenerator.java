package persistence.project.id;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.*;
import java.lang.reflect.Field;

public class DefaultIdGenerator implements IdGenerator {
  private String jsonFilePath;
  private final Gson gson = new Gson();

  private String getCurrId() {
    String id = "";

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFilePath))) {

      JsonArray jsonArray = JsonParser.parseReader(bufferedReader).getAsJsonArray();
      if (!jsonArray.isJsonNull() && !jsonArray.isEmpty()) {
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        if (jsonObject != null && jsonObject.has("currID")) {
          id = jsonObject.get("currID").getAsString();
        }
      }

    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    return id;
  }

  /*
  private void setCurrId(int id) {

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFilePath))) {
      JsonObject jsonObject = gson.fromJson(bufferedReader, JsonObject.class);
      JsonObject newJsonObject = new JsonObject();
      newJsonObject.addProperty("currID", id);

      if (jsonObject != null) {
        for (String key : jsonObject.keySet()) {
          if (!key.equals("currID")) {
            newJsonObject.add(key, jsonObject.get(key));
          }
        }
      }

      Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

      try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
        gsonPretty.toJson(newJsonObject, fileWriter);
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
*/

  private void setCurrId(int id) {
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      JsonReader reader = new JsonReader(new FileReader(jsonFilePath));
      JsonElement rootElement = JsonParser.parseReader(reader);
      reader.close();

      if (rootElement.isJsonArray()) {
        JsonArray jsonArray = rootElement.getAsJsonArray();
        if (!jsonArray.isEmpty()) {
          JsonElement firstElement = jsonArray.get(0);
          if (firstElement.isJsonObject()) {
            JsonObject firstObject = firstElement.getAsJsonObject();
            if (firstObject.has("currID")) {
              firstObject.addProperty("currID", id);
            }
          }
        }
      }

      JsonWriter writer = new JsonWriter(new FileWriter(jsonFilePath));
      gson.toJson(rootElement, writer);
      writer.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  @Override
  public String generateId(Object object, String jsonFilePath) {
    this.jsonFilePath = jsonFilePath;

    String id = getCurrId();
    setCurrId(Integer.parseInt(id) + 1);

    try {
      Class<?> objectClass = object.getClass();
      Field idField = objectClass.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(object, id);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return id;
  }
}
