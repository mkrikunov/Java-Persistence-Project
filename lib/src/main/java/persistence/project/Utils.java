package persistence.project;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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
}
