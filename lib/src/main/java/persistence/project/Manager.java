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

/**
 * Класс, представляющий собой API фрэймворка для сериализации и десериализации Java-объектов.
 */

public class Manager {

  private final Serializer serializer;
  private final Deserializer deserializer;
  private final StorageManager storageManager;
  private final Set<Object> managed = new HashSet<>();

  /**
   * Создает объект Manager с указанным путем к папке-хранилищу.
   *
   * @param storagePath путь к папке, в которой хранятся сериализованные объекты.
   */

  public Manager(String storagePath) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager,
        new DefaultIdGenerator(storageManager));
    deserializer = new Deserializer(storageManager);
  }

  /**
   * Создает объект Manager с указанным путем к папке-хранилищу и кастомизированным генератором
   * идентификаторов для сериализованных объектов.
   *
   * @param storagePath путь до папки-хранилища, где будут храниться сериализованные объекты.
   * @param idGenerator интерфейс генерации уникального идентификатора.
   */
  public Manager(String storagePath, IdGenerator idGenerator) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager, idGenerator);
    deserializer = new Deserializer(storageManager);
  }

  /**
   * Фиксирует объект, который будет помечен как управляемый и будет сериализован в хранилище после
   * вызова flush().
   *
   * @param object объект, который нужно будет сериализовать.
   */
  public void persist(Object object) {
    managed.add(object);
  }

  /**
   * Очистить набор управляемых объектов.
   */
  public void clear() {
    managed.clear();
  }

  /**
   * Удалить объект из управляемых. При вызове flush() объект также удалится из хранилища. Чтобы
   * отменить это, необходимо вызвать persist().
   *
   * @param object объект, который нужно удалить.
   */
  public void remove(Object object) {
    managed.remove(object);
    storageManager.remove(object.getClass().getName(), Utils.getObjectId(object));
  }

  /**
   * Зафиксировать изменения в хранилище.
   *
   * @throws Exception неожиданная ошибка в ходе работы.
   */
  public void flush() throws Exception {
    for (Object object : managed) {
      serializer.createObjectMap(object);
    }
    storageManager.flush();
    clear();
  }

  /**
   * Достать некоторый объект из хранилища и десериализовать его.
   *
   * @param targetClazz класс желаемого объекта.
   * @param targetId    идентификатор желаемого объекта.
   * @param <T>         тип желаемого объекта.
   * @return десериализованный объект типа T или null, если такого нет.
   */
  public <T> T retrieve(Class<T> targetClazz, int targetId) {
    var result = deserializer.deserialize(targetClazz, targetId);
    deserializer.clear();
    return result;
  }

  /**
   * Метод для поиска в хранилище записей по предикату.
   *
   * @param predicate - предикат.
   * @return - массив отфильтрованных записей.
   */
  public <T> List<T> filter(SearchPredicate predicate) {
    Map<String, JsonArray> filteredMap = storageManager.filter(predicate);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry : filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz,
            element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  /**
   * Метод для поиска в хранилище записей, не соответствующих предикату.
   *
   * @param predicate - предикат.
   * @return - массив отфильтрованных записей.
   */
  public <T> List<T> filterNot(SearchPredicate predicate) {
    Map<String, JsonArray> filteredMap = storageManager.filterNot(predicate);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry : filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz,
            element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  /**
   * Метод для поиска в хранилище записей, удовлетворяющих хотя бы одному предикату.
   *
   * @param predicates - предикаты.
   * @return - массив отфильтрованных записей.
   */
  public <T> List<T> filterOr(SearchPredicate... predicates) {
    Map<String, JsonArray> filteredMap = storageManager.filterOr(predicates);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry : filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz,
            element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }

  /**
   * Метод для поиска в хранилище записей, удовлетворяющих всем предикатам одновременно.
   *
   * @param predicates - предикаты.
   * @return - массив отфильтрованных записей.
   */
  public <T> List<T> filterAnd(SearchPredicate... predicates) {
    Map<String, JsonArray> filteredMap = storageManager.filterAnd(predicates);
    List<T> deserializedObjects = new ArrayList<>();

    for (Map.Entry<String, JsonArray> entry : filteredMap.entrySet()) {
      for (JsonElement element : entry.getValue().getAsJsonArray()) {
        Class<T> clazz;
        try {
          clazz = getClassByName(entry.getKey());
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        T deserializedObject = deserializer.deserialize(clazz,
            element.getAsJsonObject().get("id").getAsInt());
        deserializedObjects.add(deserializedObject);
      }
    }

    return deserializedObjects;
  }
}
