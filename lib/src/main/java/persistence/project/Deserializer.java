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

  public void deserialize(Object object, String id) {
    Class<?> clazz = object.getClass();
    List<Map<String, Object>> objectMaps = getObjectMaps(clazz.getName());
    Map<String, Field> allFields = getAllFields(clazz);
      for (Map<String, Object> someObjMap : objectMaps) {
        if (someObjMap.get("id").equals(id)) {
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> fields = (List<Map<String, Object>>) someObjMap.get("fields");
          Gson gson = new Gson();
          for (Map<String, Object> fieldMap : fields) {
            // состоит из одной пары ключ значение
            for (String fieldName : fieldMap.keySet()) {
              Field field1 = allFields.get(fieldName);
              field1.setAccessible(true);
              Type fieldType = field1.getType();
              Object value = gson.fromJson(fieldMap.get(fieldName).toString(), fieldType);
              try {
                field1.set(object, value);
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }
          }
          return;
        }
      }
  }

  private List<Map<String, Object>> getObjectMaps(String className) {
    String filePath = storagePath + File.separator + className + ".json";
    List<Map<String, Object>> allObjects;
    Gson gson = new Gson();
    Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    try (FileReader reader = new FileReader(filePath)) {
      allObjects = gson.fromJson(reader, listMapType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    //var currId = allObjects.get(0);
    return allObjects.subList(1, allObjects.size());
  }
}
