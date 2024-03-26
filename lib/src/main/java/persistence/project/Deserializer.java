package persistence.project;

import static persistence.project.Main.getAllFields;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
   * @param object объект, который нужно десериализовать.
   * @param id     его идентификатор в списке сериализованных объектов.
   */
  public void deserialize(Object object, int id) {
    Class<?> clazz = object.getClass();
    Map<String, Field> allFields = getAllFields(clazz);
    List<Map<String, Object>> objectMaps = getObjectsMaps(clazz.getName(), storagePath);
    Gson gson = new Gson();

    for (Map<String, Object> someObjMap : Objects.requireNonNull(objectMaps)) {
      if (gson.fromJson(someObjMap.get("id").toString(), Integer.class) != id) {
        continue;
      }

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
              field.set(object, value);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }

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
            if (Collection.class.isAssignableFrom(fieldType)) {
              Type listMapType = new TypeToken<List<Map<String, Object>>>() {
              }.getType();
              List<Map<String, Object>> listCompositeObject = gson.fromJson(
                  compositeField.get(fieldName).toString(), listMapType);
              Collection<Object> collection = new ArrayList<>();
              for (Map<String, Object> compositeObjectMap : listCompositeObject) {
                // Получаем класс объекта и id, т.е. в каком файле и под каким id его искать
                String nameClass = compositeObjectMap.get("name").toString();
                int objectId = gson.fromJson(compositeObjectMap.get("id").toString(),
                    Integer.class);
                Object compositeObject;
                try {
                  compositeObject = Class.forName(nameClass).getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException |
                         ClassNotFoundException e) {
                  System.err.println("Error when creating a new object");
                  throw new RuntimeException(e);
                }
                deserialize(compositeObject, objectId);
                collection.add(compositeObject);
              }
              try {
                field.set(object, collection);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }

              // Если это просто композитный объект
            } else {
              Type mapType = new TypeToken<Map<String, Object>>() {
              }.getType();
              Map<String, Object> compositeObjectMap = gson.fromJson(
                  compositeField.get(fieldName).toString(), mapType);
              // Получаем класс объекта и id, т.е. в каком файле и под каким id его искать
              String nameClass = compositeObjectMap.get("name").toString();
              int objectId = gson.fromJson(compositeObjectMap.get("id").toString(),
                  Integer.class);
              Object compositeObject;
              try {
                compositeObject = Class.forName(nameClass).getDeclaredConstructor().newInstance();
              } catch (InstantiationException | IllegalAccessException |
                       InvocationTargetException | NoSuchMethodException |
                       ClassNotFoundException e) {
                System.err.println("Error when creating a new object");
                throw new RuntimeException(e);
              }
              deserialize(compositeObject, objectId);
              try {
                field.set(object, compositeObject);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }
          }
        }
      }
      return;
    }
  }
}
