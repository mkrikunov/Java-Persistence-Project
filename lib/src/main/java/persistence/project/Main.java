package persistence.project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.IdGenerator;

public class Main {

  private final String filePath;

  public Main(String filePath) {
    this.filePath = filePath;
  }

  public void serialize(Object object) {

    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {

      SerializedClass serializedClassAnnotation = object.getClass().getAnnotation(SerializedClass.class);
      Class<? extends IdGenerator> idGeneratorClass = serializedClassAnnotation.idGenerator();

      String id = "";
      try {
        IdGenerator idGenerator = idGeneratorClass.getDeclaredConstructor().newInstance();
        id = idGenerator.generateId();
      } catch (InstantiationException e) {
        System.err.println(e.getMessage());
        System.exit(1);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }


      String className = object.getClass().getName();

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