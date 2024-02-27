package persistence.project;

import java.lang.reflect.Field;
import persistence.project.examples.Animal;

public class Main {

  public void serialize() {
    Animal obj = new Animal("Cat");
    Field[] fields = Animal.class.getDeclaredFields();

    System.out.println("Fields");
    for (Field field : fields) {
      field.setAccessible(true);
      Object value;
      try {
        value = field.get(obj);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      Class<?> type = field.getType();

      int modifiers = field.getModifiers();
      String access = java.lang.reflect.Modifier.toString(modifiers);

      System.out.println("Value: " + value);
      System.out.println("Type: " + type);
      System.out.println("Access: " + access);
    }
  }
}