package persistence.project;

import static persistence.project.Utils.createCollection;
import static persistence.project.Utils.getAllFields;
import static persistence.project.Utils.isCollectionOfSerializedClass;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.project.annotations.SerializedClass;

public class Deserializer {

  private final String storagePath;

  public Deserializer(String storagePath) {
    this.storagePath = storagePath;
  }

  /**
   * Считывает из .json файла данные в список мап.
   *
   * @param className   экземпляры какого класса сериализованы в .json файле.
   * @param storagePath путь до хранилища данных.
   * @return список мап, в котором в каждой мапе лежит некоторый сериализованный объект данного
   * класса. Возвращает null, если такого файла не существует.
   */
  private static List<Map<String, Object>> getObjectsMaps(String className, String storagePath) {
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

  /**
   * Десериализует некоторый объект с заданным id.
   *
   * @param clazz    класс, экземпляр которого нужно десериализовать.
   * @param targetId его идентификатор в списке сериализованных объектов.
   */
  public Object deserialize(Class<?> clazz, int targetId) {
    // Достаем все объекты из файла и все поля класса
    Map<String, Field> allFields = getAllFields(clazz);
    List<Map<String, Object>> objectMaps = getObjectsMaps(clazz.getName(), storagePath);

    // Создаем пустой целевой объект
    Object targetObject;
    try {
      targetObject = clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      System.err.println("Error during creation a target object");
      throw new RuntimeException(e);
    }

    // итерируемся по мапам объектов
    Gson gson = new Gson();
    for (Map<String, Object> someObjMap : Objects.requireNonNull(objectMaps)) {
      // Проверяем, нужный ли это id
      int id = gson.fromJson(someObjMap.get("id").toString(), Integer.class);
      if (id != targetId) {
        continue;
      }

      // Если это объект, который нам нужен
      // Сначала проходимся по всем обычным полям
      if (someObjMap.containsKey("fields")) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fields = (List<Map<String, Object>>) someObjMap.get("fields");
        for (Map<String, Object> fieldMap : fields) {
          // состоит из одной пары ключ (имя поля) и его значение.
          for (String fieldName : fieldMap.keySet()) {
            Field field = allFields.get(fieldName);
            field.setAccessible(true);
            Type fieldType = field.getType();
            Object value = gson.fromJson(fieldMap.get(fieldName).toString(), fieldType);
            try {
              field.set(targetObject, value);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }

      // Проходимся по композитным полям
      if (someObjMap.containsKey("compositeFields")) {
        // Достаем список композитных полей
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> compositeFields = (List<Map<String, Object>>) someObjMap.get(
            "compositeFields");
        // Проходимся по каждому композитному полю
        for (Map<String, Object> compositeField : compositeFields) {
          // Состоит из одной пары ключ (имя поля) и его значение. (в for будет всего 1 итерация)
          for (String fieldName : compositeField.keySet()) {
            Field field = allFields.get(fieldName);
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            // Если это коллекция композитных объектов
            if (isCollectionOfSerializedClass(field)) {
              // Достаем мапу композитных объектов
              Type listMapType = new TypeToken<List<Map<String, Object>>>() {
              }.getType();
              List<Map<String, Object>> listCompositeObject = gson.fromJson(
                  compositeField.get(fieldName).toString(), listMapType);
              // Создаем нужную коллекцию
              Collection<Object> collection = createCollection(fieldType);
              for (Map<String, Object> compositeObjectMap : listCompositeObject) {
                // Получаем класс объекта и id, т.е. в каком файле и под каким id его искать
                String nameClass = compositeObjectMap.get("name").toString();
                int objectId = gson.fromJson(compositeObjectMap.get("id").toString(),
                    Integer.class);
                Object compositeObject;
                try {
                  compositeObject = deserialize(Class.forName(nameClass), objectId);
                } catch (ClassNotFoundException e) {
                  System.err.println("Error when deserializing");
                  throw new RuntimeException(e);
                }
                collection.add(compositeObject);
              }
              try {
                field.set(targetObject, collection);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }

              // Если это просто композитный объект
            } else if (fieldType.isAnnotationPresent(SerializedClass.class)) {
              Type mapType = new TypeToken<Map<String, Object>>() {
              }.getType();
              Map<String, Object> compositeObjectMap = gson.fromJson(
                  compositeField.get(fieldName).toString(), mapType);
              int objectId = gson.fromJson(compositeObjectMap.get("id").toString(),
                  Integer.class);
              Object compositeObject = deserialize(fieldType, objectId);
              try {
                field.set(targetObject, compositeObject);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }
          }
        }
      }

      Field field = allFields.get("id");
      field.setAccessible(true);
      try {
        field.set(targetObject, id);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      return targetObject;
    }
    return null;
  }
}
