package persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StorageManager {

  private final String storagePath;
  private final Map<String, JsonArray> storage;

  public StorageManager(String storagePath) {
    this.storagePath = storagePath;
    this.storage = new HashMap<>();
  }

  /**
   * Получить JsonArray сериализованных объектов некоторого класса.
   *
   * @param className имя класса, JsonArray которого нужно получить.
   * @return JsonArray, соответствующий классу.
   */
  public JsonArray getJsonArrayByName(String className) {
    return storage.get(className);
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
   * Находит json файл заданного класса и читает из него ссериализованные объекты в JsonArray.
   * Рекомендуется проверить, что такой файл вообще существует.
   *
   * @param className имя класса, сериализованные объекты которого нужно получить в виде JsonArray.
   * @return полученный JsonArray.
   */
  public JsonArray readJsonFile(String className) {
    String jsonFilePath = storagePath + File.separator + className + ".json";
    File jsonFile = new File(jsonFilePath);
    try {
      if (jsonFile.createNewFile()) {
        return fillEmptyJsonFile(jsonFilePath);
      }
    } catch (IOException e) {
      System.err.println("Error while creating an empty json file" + e.getMessage());
      throw new RuntimeException(e);
    }

    JsonElement rootElement = null;
    try (JsonReader reader = new JsonReader(new FileReader(jsonFilePath))) {
      rootElement = JsonParser.parseReader(reader);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    if (Objects.requireNonNull(rootElement).isJsonArray()) {
      return rootElement.getAsJsonArray();
    }
    return null;
  }

  /**
   * Считывает из .json файла данные в список мап.
   *
   * @param className экземпляры какого класса сериализованы в .json файле.
   * @return список мап, в котором в каждой мапе лежит некоторый сериализованный объект данного
   * класса. Возвращает null, если такого файла не существует.
   */
  public List<Map<String, Object>> getObjectsMaps(String className) {
    String filePath = storagePath + File.separator + className + ".json";
    File jsonFile = new File(filePath);
    if (!jsonFile.exists()) {
      return null;
    }
    List<Map<String, Object>> allObjects;
    Gson gson = new Gson();
    Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    try (FileReader reader = new FileReader(filePath)) {
      allObjects = gson.fromJson(reader, listMapType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return allObjects.subList(1, allObjects.size()); // в 0 лежит currId
  }

  public Integer writeToFile(Map<String, Object> data, Field idField, Object object)
      throws IllegalAccessException {
    String jsonFilePath = storagePath + File.separator + object.getClass().getName() + ".json";
    File jsonFile = new File(jsonFilePath);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean created = false;
    try {
      created = jsonFile.createNewFile();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    int id = 0;
    try (RandomAccessFile file = new RandomAccessFile(jsonFile, "rw")) {
      if (created) {
        file.writeBytes("[\n{\n  \"currID\": 1\n}\n]");
      }

      if (idField.getInt(object) == 0) {
        //id = idGenerator.generateId(object, jsonFilePath);
        idField.set(object, id);
        data.put("id", id);
      }

      file.seek(file.length() - 1);
      file.writeBytes(",\n");

      String jsonData = gson.toJson(data);
      file.writeBytes(jsonData);

      file.writeBytes("]");
    } catch (IOException e) {
      if (!jsonFile.delete()) {
        System.err.println("Failed to delete the created file");
      }
      throw new RuntimeException(e);
    }
    return id;
  }
}
