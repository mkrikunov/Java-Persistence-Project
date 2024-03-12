package persistence.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Animal;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.id.DefaultIdGenerator;

class LibraryTest {

  @Test
  void catToJsonFile() {
    Main main;
    main = new Main("src/main/resources/storage");

    Cat murkaCat = new Cat("Murka", 2, true);
    Cat vasyaCat = new Cat("Vasya", 5, true);
    try {
      main.serialize(murkaCat);
      main.serialize(vasyaCat);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dogToJsonFile() {
    Main main;
    main = new Main("src/main/resources/storage");

    Dog bobikDog = new Dog("Bobik", 1, false);
    try {
      main.serialize(bobikDog);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dogToJsonFile2() {
    Main main;
    main = new Main("src/main/resources/storage");

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    try {
      main.serialize(muhtarDog);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void idGeneratorTest() {
    DefaultIdGenerator IdGenerator = new DefaultIdGenerator();
    ArrayList<String> idList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Animal animal = new Animal("Murka", i + 1, true);
      String id = IdGenerator.generateId(animal);
      idList.add(id);
      assertNotNull(id);
      assertFalse(id.isEmpty());
    }

    Set<String> set = new HashSet<>(idList);
    assertEquals(set.size(), idList.size()); //check for unique
  }
}
