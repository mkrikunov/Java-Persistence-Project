package persistence.project;

import static persistence.project.Utils.getAllFields;
import static persistence.project.Utils.isCollectionOfSerializedClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;

public class Serializer {

  private final String storagePath;
  private final IdGenerator idGenerator;

  public Serializer(String storagePath) {
    this.idGenerator = new DefaultIdGenerator();
    this.storagePath = storagePath;
  }

  public Serializer(String storagePath, IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
    this.storagePath = storagePath;
  }

  private Integer writeToFile(Map<String, Object> data, Field idField, Object object)
      throws IllegalAccessException {
    String jsonFilePath = storagePath + File.separator + object.getClass().getName() + ".json";
    File jsonFile = new File(jsonFilePath);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean created = false;
    try {
      created = jsonFile.createNewFile();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    int id = 0;
    try (RandomAccessFile file = new RandomAccessFile(jsonFile, "rw")) {
      if (created) {
        file.writeBytes("[\n{\n  \"currID\": 1\n}\n]");
      }

      if (idField.getInt(object) == 0) {
        id = idGenerator.generateId(object, jsonFilePath);

        data.put("id", id);
      }

      file.seek(file.length() - 1);
      file.writeBytes(",\n");

      String jsonData = gson.toJson(data);
      file.writeBytes(jsonData);

      file.writeBytes("]");
    } catch (IOException e) {
      if (!jsonFile.delete()) {
        System.err.println("Failed to delete the created file");
      }
      throw new RuntimeException(e);
    }
    return id;
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
            throw new Exception("This object has already been serialized!");
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
      return writeToFile(classMap, Objects.requireNonNull(idField), object);
    }
    System.out.println("Class " + object.getClass().getName()
        + " isn't marked with an annotation SerializedClass");
    return 0;
  }
}