package persistence.project;

import static persistence.project.Utils.findById;

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
import java.util.function.Predicate;

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

  void remove(String className, int id) {
    var jsonArray = getJsonArrayByClassName(className);
    Gson gson = new Gson();
    var foundObjectMap = findById(id, className, this);
    if (foundObjectMap == null) {
      return;
    }
    jsonArray.remove(gson.toJsonTree(foundObjectMap));
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

  /**
   * Метод для поиска в хранилище записей по предикату.
   *
   * @param searchPredicate - предикат
   * @return - массив отфильтрованных записей
   */
  public Map<String, JsonArray> filter(SearchPredicate searchPredicate) {
    Predicate<JsonElement> predicate = searchPredicate.getPredicate();

    Map<String, JsonArray> filteredMap = new HashMap<>();
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        //Пропускаем элемент с currID
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        if (predicate.test(element)) {
          filteredArray.add(element);
        }
      }
      if (!filteredArray.isEmpty()) {
        filteredMap.put(entry.getKey(), filteredArray);
      }
    }
    return filteredMap;
  }

  /**
   * Метод для поиска в хранилище записей, не соответствующих предикату.
   *
   * @param searchPredicate - предикат
   * @return - массив отфильтрованных записей
   */
  public Map<String, JsonArray> filterNot(SearchPredicate searchPredicate) {
    Predicate<JsonElement> predicate = searchPredicate.getPredicate();

    Map<String, JsonArray> filteredMap = new HashMap<>();
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        //Пропускаем элемент с currID
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        if (!predicate.test(element)) {
          filteredArray.add(element);
        }
      }
      if (!filteredArray.isEmpty()) {
        filteredMap.put(entry.getKey(), filteredArray);
      }
    }
    return filteredMap;
  }

  /**
   * Метод для поиска в хранилище записей,
   * удовлетворяющих хотя бы одному предикату.
   *
   * @param searchPredicates - предикаты
   * @return - массив отфильтрованных записей
   */
  public Map<String, JsonArray> filterOr(SearchPredicate... searchPredicates) {
    Map<String, JsonArray> filteredMap = new HashMap<>();
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        for (SearchPredicate searchPredicate : searchPredicates) {
          Predicate<JsonElement> predicate = searchPredicate.getPredicate();
          if (predicate.test(element)) {
            filteredArray.add(element);
            break;
          }
        }
      }
      if (!filteredArray.isEmpty()) {
        filteredMap.put(entry.getKey(), filteredArray);
      }
    }
    return filteredMap;
  }

  /**
   * Метод для поиска в хранилище записей,
   * удовлетворяющих всем предикатам.
   *
   * @param searchPredicates - предикаты
   * @return - массив отфильтрованных записей
   */
  public Map<String, JsonArray> filterAnd(SearchPredicate... searchPredicates) {
    Map<String, JsonArray> filteredMap = new HashMap<>();
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        boolean allMatch = true;
        for (SearchPredicate searchPredicate : searchPredicates) {
          Predicate<JsonElement> predicate = searchPredicate.getPredicate();
          if (!predicate.test(element)) {
            allMatch = false;
            break;
          }
        }
        if (allMatch) {
          filteredArray.add(element);
        }
      }
      if (!filteredArray.isEmpty()) {
        filteredMap.put(entry.getKey(), filteredArray);
      }
    }
    return filteredMap;
  }
}