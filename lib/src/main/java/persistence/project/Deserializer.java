package persistence.project;

import static persistence.project.Utils.createCollection;
import static persistence.project.Utils.getAllFields;
import static persistence.project.Utils.isCollectionOfSerializedClass;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import persistence.project.annotations.SerializedClass;

public class Deserializer {

  private final StorageManager storageManager;

  Deserializer(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  /**
   * Преобразует JsonArray в список Map, где каждая Map'а представляет собой сериализованный
   * объект.
   *
   * @param className экземпляры какого класса сериализованы в .json файле.
   * @return список мап, в котором в каждой Map'е лежит некоторый сериализованный объект данного
   * класса. Возвращает null, если такого файла не существует.
   */
  public List<Map<String, Object>> getObjectsMaps(String className) {
    JsonArray jsonArray = storageManager.getJsonArrayByClassName(className);
    if (jsonArray == null || jsonArray.size() == 1) {
      return new ArrayList<>();
    }
    Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    List<Map<String, Object>> allObjects = new Gson().fromJson(jsonArray, listMapType);
    return allObjects.subList(1, allObjects.size()); // в 0 лежит currId
  }

  /**
   * Десериализует некоторый объект с заданным id.
   *
   * @param clazz    класс, экземпляр которого нужно десериализовать.
   * @param targetId его идентификатор в списке сериализованных объектов.
   */
  public Object deserialize(Class<?> clazz, int targetId) {
    List<Map<String, Object>> objectMaps = getObjectsMaps(clazz.getName()); //Достаем все объекты...
    Map<String, Field> allFields = getAllFields(clazz); // ...из файла и все поля класса

    Object targetObject;
    try {
      targetObject = clazz.getDeclaredConstructor().newInstance();  // Создаем пустой целевой объект
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      System.err.println("Error during creation a target object");
      throw new RuntimeException(e);
    }
    Gson gson = new Gson();
    for (Map<String, Object> someObjMap : objectMaps) { // итерируемся по Map'ам объектов
      int id = gson.fromJson(someObjMap.get("id").toString(), Integer.class);
      if (id != targetId) { // Проверяем, нужный ли это id
        continue;
      }
      if (someObjMap.containsKey("fields")) { // Сначала проходимся по всем обычным полям
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
              // Достаем Map'у композитных объектов
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
