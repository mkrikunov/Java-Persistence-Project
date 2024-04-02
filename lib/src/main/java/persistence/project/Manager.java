package persistence.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;
import static persistence.project.Utils.getClassByName;

public class Manager {

  private final Serializer serializer;
  private final Deserializer deserializer;
  private final StorageManager storageManager;
  private final Set<Object> managed = new HashSet<>();

  public Manager(String storagePath) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager,
        new DefaultIdGenerator(storageManager));
    deserializer = new Deserializer(storageManager);
  }

  public Manager(String storagePath, IdGenerator idGenerator) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager, idGenerator);
    deserializer = new Deserializer(storageManager);
  }

  public void persist(Object object) {
    managed.add(object);
  }

  public void clear() {
    managed.clear();
  }

  public void remove(Object object) {
    managed.remove(object);
    storageManager.remove(object.getClass().getName(), Utils.getObjectId(object));
  }

  public void flush() throws Exception {
    for (Object object : managed) {
      serializer.createObjectMap(object);
    }
    storageManager.flush();
    clear();
  }

  public <T> T retrieve(Class<T> targetClazz, int targetId) {
    return deserializer.deserialize(targetClazz, targetId);
  }



  public <T> List<T> filter(SearchPredicate predicate) {
    Map<String, JsonArray> filteredMap = storageManager.filter(predicate);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry: filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz, element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  public <T> List<T> filterNot(SearchPredicate predicate) {
    Map<String, JsonArray> filteredMap = storageManager.filterNot(predicate);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry: filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz, element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  public <T> List<T> filterOr(SearchPredicate... predicates) {
    Map<String, JsonArray> filteredMap = storageManager.filterOr(predicates);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry: filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz, element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  public <T> List<T> filterAnd(SearchPredicate... predicates) {
    Map<String, JsonArray> filteredMap = storageManager.filterAnd(predicates);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry: filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz, element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }
}
