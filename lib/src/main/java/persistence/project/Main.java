package persistence.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import persistence.project.annotations.ID;
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

  private void writeToFile(Object data, String className) throws ClassNotFoundException {
    String jsonFilePath = folderPath + File.separator + className + ".json";
    File jsonFile = new File(jsonFilePath);
    Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .create();

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

      ////////////////
      Wrapper wrapper = new Wrapper(data);
      String jsonData = gson.toJson(wrapper);
      ////////////////
      //String jsonData = gson.toJson(data);
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
      String id;
      for (Field field : object.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(ID.class)) {
          field.setAccessible(true);
          if (field.getInt(object) == 0) {
            id = idGenerator.generateId(object);
            field.setAccessible(true);
            field.set(object, id);
          }
        }
      }
      String className = object.getClass().getName();
      try {
        writeToFile(object, className);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    } else {
      System.out.println("Class " + object.getClass().getName()
          + " isn't marked with an annotation SerializedClass");
    }
  }
}