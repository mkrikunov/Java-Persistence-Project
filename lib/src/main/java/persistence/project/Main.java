package persistence.project;

import static persistence.project.Deserializer.getObjectMaps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.DefaultIdGenerator;

public class Main {

  private final String storagePath;
  private final DefaultIdGenerator idGenerator;

  public Main(String storagePath) {
    this.idGenerator = new DefaultIdGenerator();
    this.storagePath = storagePath;
  }

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

  private void writeToFile(Map<String, Object> data, Field idField, Object object)
      throws IllegalAccessException {
    String jsonFilePath = storagePath + File.separator + data.get("name") + ".json";
    File jsonFile = new File(jsonFilePath);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean created = false;
    try {
      created = jsonFile.createNewFile();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    if (idField.getInt(object) == 0) {
      //var id = idGenerator.generateId(object, jsonFilePath);
      var id = "1";
      data.put("id", id);
    }

    try (RandomAccessFile file = new RandomAccessFile(jsonFile, "rw")) {
      if (created) {
        file.writeBytes("[\n{\n  \"currID\": 1\n}\n]");
        file.seek(file.length() - 1);
      } else {
        file.seek(file.length() - 2);
        file.writeBytes(",");
      }

      String jsonData = gson.toJson(data);
      file.writeBytes(jsonData);

      file.writeBytes("]");
    } catch (IOException e) {
      if (!jsonFile.delete()) {
        System.err.println("Failed to delete the created file");
      }
      throw new RuntimeException(e);
    }/* catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }*/
  }

  public static boolean isCollectionOfSerializedClass(Field field) {
    if (Collection.class.isAssignableFrom(field.getType())) {
      ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
      Class<?> elementType = (Class<?>) fieldType.getActualTypeArguments()[0];
      return elementType.isAnnotationPresent(SerializedClass.class);
    }
    return false;
  }

  private Integer findOrSerialize(Object object) throws Exception {
    var allFields = getAllFields(object.getClass());
    Field fieldId = allFields.get("id");
    fieldId.setAccessible(true);
    int objId = Integer.parseInt(fieldId.get(object).toString());
    if (objId == 0) {
      serialize(object);
      allFields = getAllFields(object.getClass());
      var idField = allFields.get("id");
      idField.setAccessible(true);
      return Integer.valueOf(idField.get(object).toString());
    }
    List<Map<String, Object>> objectMaps = getObjectMaps(object.getClass().getName(),
        storagePath);
    if (objectMaps != null) {
      for (Map<String, Object> objectMap : objectMaps) {
        if (objectMap.get("id").equals(objId)) {
          return objId;
        }
      }
    }
    throw new Exception("Something went wrong");
  }

  public void serialize(Object object) throws IllegalAccessException {
    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {
      Map<String, Object> classMap = new LinkedHashMap<>(4);
      classMap.put("name", object.getClass().getName());

      Map<String, Field> allFields = getAllFields(object.getClass());
      List<Map<String, Object>> fields = new ArrayList<>();
      List<Map<String, Object>> compositeFields = new ArrayList<>();
      Field idField = null;
      for (String fieldName : allFields.keySet()) {
        Field field = allFields.get(fieldName);
        field.setAccessible(true);
        if (field.isAnnotationPresent(ID.class)) {
          idField = field;
          continue;
        }
        Class<?> fieldType = field.getType();
        Map<String, Object> someField = new LinkedHashMap<>(2);
        try {
          if (field.get(object) == null) {
            continue;
          }
          if (fieldType.isAnnotationPresent(SerializedClass.class)) {
            try {
              someField.put(fieldName, findOrSerialize(field.get(object)));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
            compositeFields.add(someField);
          } else if (isCollectionOfSerializedClass(field)) {
            /*@SuppressWarnings("unchecked")
            Collection<Object> collection = (Collection<Object>) field.get(object);
            List<Map<String, Object>> listObjects = new ArrayList<>(collection.size());
            for (Object element : collection) {
              listObjects.add(findOrSerialize(element));
            }
            someField.put(fieldName, listObjects);
            compositeFields.add(someField);*/
          } else {
            someField.put(fieldName, field.get(object));
            fields.add(someField);
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
      if (!fields.isEmpty()) {
        classMap.put("fields", fields);
      }
      if (!compositeFields.isEmpty()) {
        classMap.put("compositeFields", compositeFields);
      }
      writeToFile(classMap, idField, object);
    } else {
      System.out.println("Class " + object.getClass().getName()
          + " isn't marked with an annotation SerializedClass");
    }
  }
}