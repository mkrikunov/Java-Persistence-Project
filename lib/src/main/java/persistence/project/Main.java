package persistence.project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private final String filePath;

  public Main(String filePath) {
    this.filePath = filePath;
  }

  public void serialize(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();
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

    writeToFile(values, types, modifiers, names, n);
  }

  private void writeToFile(List<Object> values, List<Class<?>> types, List<String> modifiers,
      List<String> names, int n) {
    try (OutputStream outputStream = new FileOutputStream(filePath, true)) {
      outputStream.write("{\n".getBytes());
      outputStream.write("  \"class\": {\n".getBytes());
      outputStream.write("    \"id\": 1,\n".getBytes());
      outputStream.write("    \"name\": \"ExampleClass\",\n".getBytes());
      outputStream.write("    \"fields\": [\n      {\n".getBytes());

      for (int i = 0; i < n; i++) {
        outputStream.write(("        \"name\": \"" + names.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"type\": \"" + types.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"value\": \"" + values.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"access\": \"" + modifiers.get(i) + "\"\n      }\n").getBytes());
      }

      outputStream.write("    ]\n  }\n}".getBytes());
    } catch (IOException e) {
      System.out.println("Ошибка при добавлении данных в файл: " + e.getMessage());
    }
  }
}