package persistence.project;

import static persistence.project.Main.getAllFields;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Deserializer {

  private final String storagePath;

  public Deserializer(String storagePath) {
    this.storagePath = storagePath;
  }

  public static String getFileNameWithoutExtension(File file) {
    String fileName = file.getName();
    int lastIndex = fileName.lastIndexOf('.');
    if (lastIndex > 0) {
      return fileName.substring(0, lastIndex);
    }
    return fileName;
  }

  private Field findMeededField(Field[] fields, String nameField) throws NoSuchFieldException {
    for (Field field : fields) {
      if (field.getName().equals(nameField)) {
        return field;
      }
    }
    throw new NoSuchFieldException();
  }

  private void jsonToObject(Object object, List<Map<String, Object>> fields, Gson gson)
      throws NoSuchFieldException {
    Field[] allFields = null;

    for (Map<String, Object> fieldFromJson : fields) {
      Field field1;
      var nameField = fieldFromJson.get("name").toString();
      try {
        field1 = object.getClass().getDeclaredField(nameField);
      } catch (NoSuchFieldException e) {
        if (allFields == null) {
          allFields = getAllFields(object.getClass());
        }
        field1 = findMeededField(allFields, nameField);
      }
      field1.setAccessible(true);
      Class<?> fieldType = field1.getType();
      Object parsedValue = gson.fromJson(gson.toJson(fieldFromJson.get("value")), fieldType);
      try {
        field1.set(object, parsedValue);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void deserialize(Object object, String id) {
    String filePath = storagePath + File.separator + object.getClass().getName() + ".json";
    try (FileReader reader = new FileReader(filePath)) {
      Gson gson = new Gson();
      Type listMapType = new TypeToken<List<Map<String, Object>>>() {
      }.getType();
      List<Map<String, Object>> allObjects = gson.fromJson(reader, listMapType);
      //var currId = allObjects.get(0);
      allObjects = allObjects.subList(1, allObjects.size());
      for (Map<String, Object> someObj : allObjects) {
        if (someObj.get("id").equals(id)) {
          System.out.println(someObj);
          List<Map<String, Object>> parsedValue = gson.fromJson(gson.toJson(someObj.get("fields")),
              listMapType);
          jsonToObject(object, parsedValue, gson);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
}
