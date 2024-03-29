package persistence.project;

import static persistence.project.Utils.getAllFields;
import static persistence.project.Utils.isCollectionOfSerializedClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

public class Serializer {

  private final StorageManager storageManager;

  public Serializer(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  private Integer findOrSerialize(Object object) throws Exception {
    // Получаем Id объекта
    var allFields = getAllFields(object.getClass());
    Field fieldId = allFields.get("id");
    fieldId.setAccessible(true);
    int objId = fieldId.getInt(object);
    // Если объект уже сериализован -> возвращаем Id
    if (objId != 0) {
      return objId;
    }
    // иначе сериализуем
    return serialize(object);
  }


  public Integer serialize(Object object) throws Exception {
    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {
      Map<String, Object> classMap = new LinkedHashMap<>(3);

      Map<String, Field> allFields = getAllFields(object.getClass());
      List<Map<String, Object>> fields = new ArrayList<>();
      List<Map<String, Object>> compositeFields = new ArrayList<>();
      Field idField = null;

      for (String fieldName : allFields.keySet()) {
        Field field = allFields.get(fieldName);
        field.setAccessible(true);

        // Если поле - ID
        if (field.isAnnotationPresent(ID.class)) {
          idField = field;
          // Если объект уже был сериализован
          if (idField.getInt(object) != 0) {
            System.err.println("This object has already been serialized!");
            return idField.getInt(object);
          }
          continue;
        }

        var fieldValue = field.get(object);
        // Если поле без значения
        if (fieldValue == null) {
          continue;
        }

        Map<String, Object> someField = new LinkedHashMap<>(1);
        if (field.getType().isAnnotationPresent(SerializedClass.class)) {
          Map<String, Object> compositeObjectMap = new LinkedHashMap<>(2);
          compositeObjectMap.put("name", fieldValue.getClass().getName());
          compositeObjectMap.put("id", findOrSerialize(fieldValue));
          someField.put(fieldName, compositeObjectMap);
          compositeFields.add(someField);

        } else if (isCollectionOfSerializedClass(field)) {
          @SuppressWarnings("unchecked")
          Collection<Object> collection = (Collection<Object>) fieldValue;
          List<Map<String, Object>> listObjects = new ArrayList<>(collection.size());
          for (Object element : collection) {
            Map<String, Object> compositeObjectMap = new LinkedHashMap<>(2);
            compositeObjectMap.put("name", element.getClass().getName());
            compositeObjectMap.put("id", findOrSerialize(element));
            listObjects.add(compositeObjectMap);
          }
          someField.put(fieldName, listObjects);
          compositeFields.add(someField);
        } else {
          someField.put(fieldName, fieldValue);
          fields.add(someField);
        }
      }
      // Добавляем наборы полей в мапу объекта
      if (!fields.isEmpty()) {
        classMap.put("fields", fields);
      }
      if (!compositeFields.isEmpty()) {
        classMap.put("compositeFields", compositeFields);
      }
      return storageManager.writeToFile(classMap, Objects.requireNonNull(idField), object);
    }
    System.out.println("Class " + object.getClass().getName()
        + " isn't marked with an annotation SerializedClass");
    return 0;
  }
}