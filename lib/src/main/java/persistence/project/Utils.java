package persistence.project;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import persistence.project.annotations.SerializedClass;

public class Utils {

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
}
