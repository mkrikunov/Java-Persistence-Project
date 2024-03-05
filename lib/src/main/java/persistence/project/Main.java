package persistence.project;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class Main {

  private final OutputStream outputStream;

  public Main(String filePath) throws FileNotFoundException {
    outputStream = new FileOutputStream(filePath);
  }

  public void serialize(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      Object value;
      try {
        value = field.get(object);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      Class<?> type = field.getType();

      int modifiers = field.getModifiers();
      String access = java.lang.reflect.Modifier.toString(modifiers);

      //outputStream.write();
      System.out.println("Value: " + value);
      System.out.println("Type: " + type);
      System.out.println("Access: " + access);
    }
  }


}