package persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import persistence.project.annotations.ID;
import java.util.Map;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;

public class Main {

  private final String folderPath;
  private final DefaultIdGenerator idGenerator;

  public Main(String folderPath) {
    this.idGenerator = new DefaultIdGenerator();
    this.folderPath = folderPath;
  }

  public static Field[] getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    while (clazz != null) {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fields.toArray(new Field[0]);
  }

  private Map<String, Object> dataToMap(String className, String id, List<Object> values,
      List<Class<?>> types,
      List<String> modifiers, List<String> names, int nFields) {
    Map<String, Object> classMap = new LinkedHashMap<>(3);
    classMap.put("id", id);
    classMap.put("name", className);

    List<Map<String, Object>> fields = new ArrayList<>(nFields);
    for (int i = 0; i < nFields; i++) {
      Map<String, Object> someField = new LinkedHashMap<>(nFields);
      someField.put("name", names.get(i));
      someField.put("type", types.get(i).getName());
      someField.put("value", values.get(i));
      someField.put("access", modifiers.get(i));
      fields.add(someField);
    }

    classMap.put("fields", fields);
    return classMap;
  }

  private void writeToFile(Map<String, Object> data) {
    String jsonFilePath = folderPath + File.separator + data.get("name") + ".json";
    File jsonFile = new File(jsonFilePath);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    boolean created = false;
    try {
      created = jsonFile.createNewFile();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    try (RandomAccessFile file = new RandomAccessFile(jsonFile, "rw")) {
      if (created) {
        file.writeBytes("[\n]");
        file.seek(file.length() - 1);
      } else {
        file.seek(file.length() - 1);
        file.writeBytes(",");
      }

      String jsonData = gson.toJson(data);
      file.writeBytes(jsonData);

      file.writeBytes("]");
    } catch (IOException e) {
      if (jsonFile.delete()) {
        jsonFile.delete();
      }
      throw new RuntimeException(e);
    }
  }

  public void serialize(Object object) throws IllegalAccessException {
    serialize(object, idGenerator);
  }

  public void serialize(Object object, IdGenerator idGenerator) throws IllegalAccessException {

    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {

      String id = "";
      Field[] fields = getAllFields(object.getClass());
      for (Field field : fields) {
        if (field.isAnnotationPresent(ID.class)) {
          if (field.getInt(object) == 0) {
            id = idGenerator.generateId(object);
          }
        }
      }

      String className = object.getClass().getName();

      int n = fields.length;
      List<String> names = new ArrayList<>(n);
      List<Object> values = new ArrayList<>(n);
      List<Class<?>> types = new ArrayList<>(n);
      List<String> modifiers = new ArrayList<>(n);

      for (Field field : fields) {
        field.setAccessible(true);
        names.add(field.getName());

        try {
          values.add(field.get(object));
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
        types.add(field.getType());

        int modifiersField = field.getModifiers();
        String access = java.lang.reflect.Modifier.toString(modifiersField);
        modifiers.add(access);
      }

      writeToFile(dataToMap(className, id, values, types, modifiers, names, n));


    } else {
      System.out.println("Class " + object.getClass().getName()
          + " isn't marked with an annotation SerializedClass");
    }
  }
}