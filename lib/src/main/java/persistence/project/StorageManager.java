package persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StorageManager {

  private final String storagePath;
  private final Map<String, JsonArray> storage;
  private final Set<String> changedClassesNames;

  StorageManager(String storagePath) {
    this.storagePath = storagePath;
    storage = new HashMap<>();
    changedClassesNames = new HashSet<>();
  }

  /**
   * Заполняет пустой json файл, записывая туда currId равный 1. Предполагает, что пустой файл уже
   * был создан.
   *
   * @param jsonFilePath путь до целевого пустого файла.
   * @return записанный в данный файл JsonArray.
   */
  private JsonArray fillEmptyJsonFile(String jsonFilePath) {
    Map<String, Integer> currId = new HashMap<>();
    currId.put("currID", 1);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonArray jsonArray = new JsonArray(1);
    jsonArray.add(gson.toJsonTree(currId));

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFilePath))) {
      gson.toJson(jsonArray, writer);
    } catch (IOException e) {
      System.err.println("Error while filling an empty json file" + e.getMessage());
      throw new RuntimeException(e);
    }

    return jsonArray;
  }

  /**
   * Находит JsonArray сериализованных объектов данного класса.
   *
   * @param className имя класса, сериализованные объекты которого нужно получить в виде JsonArray.
   * @return полученный JsonArray.
   */
  public JsonArray getJsonArrayByClassName(String className) {
    if (storage.containsKey(className)) {
      return storage.get(className);
    }

    String jsonFilePath = storagePath + File.separator + className + ".json";
    File jsonFile = new File(jsonFilePath);
    JsonArray jsonArray = null;
    boolean created;
    try {
      created = jsonFile.createNewFile();
    } catch (IOException e) {
      System.err.println("Error while creating an empty json file" + e.getMessage());
      throw new RuntimeException(e);
    }

    if (created) {
      jsonArray = fillEmptyJsonFile(jsonFilePath);
    } else {
      JsonElement rootElement = null;
      try (JsonReader reader = new JsonReader(new FileReader(jsonFilePath))) {
        rootElement = JsonParser.parseReader(reader);
      } catch (IOException e) {
        System.err.println("Something went wrong" + e.getMessage());
      }
      if (Objects.requireNonNull(rootElement).isJsonArray()) {
        jsonArray = rootElement.getAsJsonArray();
      }
    }

    storage.put(className, jsonArray);
    return jsonArray;
  }

  void updateStorage(Map<String, Object> data, String className) {
    JsonArray jsonArray = getJsonArrayByClassName(className);
    Gson gson = new Gson();
    jsonArray.add(gson.toJsonTree(data));
    changedClassesNames.add(className);
  }

  void updateStorage(int index, Map<String, Object> data, String className) {
    JsonArray jsonArray = getJsonArrayByClassName(className);
    Gson gson = new Gson();
    jsonArray.set(index, gson.toJsonTree(data));
    changedClassesNames.add(className);
  }

  void remove(String className, int index) {
    var jsonArray = getJsonArrayByClassName(className);
    jsonArray.remove(index);
    changedClassesNames.add(className);
  }

  void flush() throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    for (String className : changedClassesNames) {
      String jsonFilePath = storagePath + File.separator + className + ".json";
      BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFilePath));
      gson.toJson(storage.get(className), writer);
      writer.close();
    }
    changedClassesNames.clear();
  }
}