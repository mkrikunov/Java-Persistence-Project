package persistence.project;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import persistence.project.annotations.SerializedClass;

public class Utils {

  /**
   * Получить Map'у всех полей объекта, в которой ключом является имя поля, а значением само поле.
   *
   * @param clazz поля какого класса нужно получить.
   * @return Map всех полей объекта.
   */
  public static Map<String, Field> getAllFields(Class<?> clazz) {
    Map<String, Field> fields = new HashMap<>();
    while (clazz != null) {
      Field[] declaredFields = clazz.getDeclaredFields();
      for (Field field : declaredFields) {
        fields.put(field.getName(), field);
      }
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  public static Collection<Object> createCollection(Class<?> fieldType) {
    if (List.class.isAssignableFrom(fieldType)) {
      return new ArrayList<>();
    } else if (Set.class.isAssignableFrom(fieldType)) {
      return new HashSet<>();
    } else {
      throw new IllegalArgumentException("Unsupported collection type: " + fieldType);
    }
  }

  public static boolean isCollectionOfSerializedClass(Field field) {
    if (Collection.class.isAssignableFrom(field.getType())) {
      ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
      Class<?> elementType = (Class<?>) fieldType.getActualTypeArguments()[0];
      return elementType.isAnnotationPresent(SerializedClass.class);
    }
    return false;
  }

  public static boolean mapsAreNotEqual(Map<?, ?> map1, Map<?, ?> map2) {
    if (map1.size() != map2.size()) {
      return true;
    }
    for (Map.Entry<?, ?> entry : map1.entrySet()) {
      Object key = entry.getKey();
      Object value1 = entry.getValue();
      Object value2 = map2.get(key);

      if (value1 instanceof Map && value2 instanceof Map) {
        if (mapsAreNotEqual((Map<?, ?>) value1, (Map<?, ?>) value2)) {
          return true;
        }
      } else {
        if (!Objects.equals(value1, value2)) {
          return true;
        }
      }
    }
    return false;
  }

  public static int getObjectId(Object object) {
    var allFields = getAllFields(object.getClass());
    Field fieldId = allFields.get("id");
    fieldId.setAccessible(true);
    int objId = 0;
    try {
      objId = fieldId.getInt(object);
    } catch (IllegalAccessException e) {
      System.err.println(e.getMessage());
    }
    return objId;
  }

  public static Map<String, Object> findById(int targetId, String className,
      StorageManager storageManager) {
    JsonArray jsonArray = storageManager.getJsonArrayByClassName(className);
    List<Map<String, Object>> allObjectsMaps;
    if (jsonArray == null || jsonArray.size() == 1) {
      return null;
    }
    Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    Gson gson = new Gson();
    allObjectsMaps = gson.fromJson(jsonArray, listMapType);
    for (Map<String, Object> someObjMap : allObjectsMaps.subList(1,
        allObjectsMaps.size())) { // итерируемся по Map'ам объектов
      int id = gson.fromJson(someObjMap.get("id").toString(), Integer.class);
      if (id == targetId) { // Проверяем, нужный ли это id
        return someObjMap;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getClassByName(String className) throws ClassNotFoundException {
    return (Class<T>) Class.forName(className);
  }
}
