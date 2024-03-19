package persistence.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Deserializer {

  public static String getFileNameWithoutExtension(File file) {
    String fileName = file.getName();
    int lastIndex = fileName.lastIndexOf('.');
    if (lastIndex > 0) {
      return fileName.substring(0, lastIndex);
    }
    return fileName;
  }

  public void deserialize(String filePath) throws ClassNotFoundException {
    Class<?> clazz = Class.forName(getFileNameWithoutExtension(new File(filePath)));
    Type listType = TypeToken.getParameterized(List.class, clazz).getType();
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(filePath)) {
      List<?> list = gson.fromJson(reader, listType);
      for (Object object : list) {
        System.out.println(object);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
