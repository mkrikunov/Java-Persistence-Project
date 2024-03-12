package persistence.project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;

public class Main {

  private final String filePath;
  private final DefaultIdGenerator idGenerator;

  public Main(String filePath) {
    this.idGenerator = new DefaultIdGenerator();
    this.filePath = filePath;
  }

  public void serialize(Object object) throws IllegalAccessException {
    serialize(object, idGenerator);
  }

  public void serialize(Object object, IdGenerator idGenerator) throws IllegalAccessException {

    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {

      String id = "";
      Field[] fields = object.getClass().getDeclaredFields();
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
      writeToFile(className, id, values, types, modifiers, names, n);


    } else {
      System.out.println("Класс " + object.getClass().getName() + " не помечен аннотацией SerializedClass");
    }
  }

  private void writeToFile(String className, String id, List<Object> values, List<Class<?>> types,
      List<String> modifiers, List<String> names, int n) {
    try (OutputStream outputStream = new FileOutputStream(filePath, true)) {
      outputStream.write("{\n".getBytes());
      outputStream.write("  \"class\": {\n".getBytes());
      outputStream.write(("    \"id\": " + id + ",\n").getBytes());
      outputStream.write(("    \"name\": \"" + className + "\",\n").getBytes());
      outputStream.write("    \"fields\": [\n".getBytes());

      for (int i = 0; i < n; i++) {
        outputStream.write("      {\n".getBytes());
        outputStream.write(("        \"name\": \"" + names.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"type\": \"" + types.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"value\": \"" + values.get(i) + "\",\n").getBytes());
        outputStream.write(("        \"access\": \"" + modifiers.get(i) + "\"\n      }").getBytes());
        if (i == n - 1) {
          outputStream.write("\n".getBytes());
        } else {
          outputStream.write(",\n".getBytes());
        }
      }

      outputStream.write("    ]\n  }\n}".getBytes());
    } catch (IOException e) {
      System.out.println("Ошибка при добавлении данных в файл: " + e.getMessage());
    }
  }
}