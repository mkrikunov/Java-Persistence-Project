package persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;

public class StorageManager {

  private final IdGenerator idGenerator;

  private final String storagePath;

  public StorageManager(String storagePath) {
    this.storagePath = storagePath;
    this.idGenerator = new DefaultIdGenerator();
  }

  public StorageManager(IdGenerator idGenerator, String storagePath) {
    this.storagePath = storagePath;
    this.idGenerator = idGenerator;
  }

  /**
   * Считывает из .json файла данные в список мап.
   *
   * @param className   экземпляры какого класса сериализованы в .json файле.
   * @return список мап, в котором в каждой мапе лежит некоторый сериализованный объект данного
   * класса. Возвращает null, если такого файла не существует.
   */
  public List<Map<String, Object>> getObjectsMaps(String className) {
    String filePath = storagePath + File.separator + className + ".json";
    File jsonFile = new File(filePath);
    if (!jsonFile.exists()) {
      return null;
    }
    List<Map<String, Object>> allObjects;
    Gson gson = new Gson();
    Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    try (FileReader reader = new FileReader(filePath)) {
      allObjects = gson.fromJson(reader, listMapType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return allObjects.subList(1, allObjects.size()); // в 0 лежит currId
  }

  public Integer writeToFile(Map<String, Object> data, Field idField, Object object)
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
        idField.set(object, id);
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
}
