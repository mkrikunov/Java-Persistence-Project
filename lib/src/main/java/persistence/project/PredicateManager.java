package persistence.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Predicate;

public class PredicateManager {

  private final Map<String, JsonArray> storage;

  public PredicateManager(Map<String, JsonArray> storage) {
    this.storage = storage;
  }

  /**
   * Метод, который используется при построении предиката, работающего с полями.
   * <p>
   * Пример использования:
   * Predicate<JsonElement> predicate = element -> {
   *       String nameAnimal = predicateManager.searchField(element, "nameAnimal");
   *       return Objects.equals(nameAnimal, "Bob");
   *     };
   *
   * @param element - JsonElement
   * @param fieldName - имя поля, которое мы ищем
   * @return - значение этого поля в данном элементе
   */
  public String searchField(JsonElement element, String fieldName) {
    JsonArray fieldsArray = element.getAsJsonObject().get("fields").getAsJsonArray();
    String fieldValue = "";
    for (JsonElement elem: fieldsArray) {
      fieldValue = elem.getAsJsonObject().get(fieldName).getAsString();
      if (!fieldValue.isEmpty()) break;
    }
    return fieldValue;
  }

  /**
   * Метод, который используется при построении предиката, работающего с ID.
   * <p>
   * Пример использования:
   * Predicate<JsonElement> predicate = element -> {
   *       int id = predicateManager.searchID(element);
   *       return id == 1;
   *     };
   *
   * @param element - JsonElement
   * @return - значение ID в этом элементе
   */
  public int searchID(JsonElement element) {
    return element.getAsJsonObject().get("id").getAsInt();
  }

  /**
   * Метод для поиска в хранилище записей по предикату.
   *
   * @param predicate - предикат
   * @return - массив отфильтрованных записей
   */
  public JsonArray filter(Predicate<JsonElement> predicate) {
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
    }
    return filteredArray;
  }

  /**
   * Метод для поиска в хранилище записей, не соответствующих предикату.
   *
   * @param predicate - предикат
   * @return - массив отфильтрованных записей
   */
  public JsonArray filterNot(Predicate<JsonElement> predicate) {
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
    }
    return filteredArray;
  }

  /**
   * Метод для поиска в хранилище записей,
   * удовлетворяющих хотя бы одному предикату.
   *
   * @param predicates - предикаты
   * @return - массив отфильтрованных записей
   */
  @SafeVarargs
  public final JsonArray filterOr(Predicate<JsonElement>... predicates) {
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        for (Predicate<JsonElement> predicate : predicates) {
          if (predicate.test(element)) {
            filteredArray.add(element);
            break;
          }
        }
      }
    }
    return filteredArray;
  }

  /**
   * Метод для поиска в хранилище записей,
   * удовлетворяющих всем предикатам.
   *
   * @param predicates - предикаты
   * @return - массив отфильтрованных записей
   */
  @SafeVarargs
  public final JsonArray filterAnd(Predicate<JsonElement>... predicates) {
    JsonArray filteredArray = new JsonArray();
    for (Map.Entry<String, JsonArray> entry: storage.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        if (element.getAsJsonObject().has("currID")) {
          continue;
        }
        boolean allMatch = true;
        for (Predicate<JsonElement> predicate : predicates) {
          if (!predicate.test(element)) {
            allMatch = false;
            break;
          }
        }
        if (allMatch) {
          filteredArray.add(element);
        }
      }
    }
    return filteredArray;
  }
}
