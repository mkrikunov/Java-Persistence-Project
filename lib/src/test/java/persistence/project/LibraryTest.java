package persistence.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Animal;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.id.DefaultIdGenerator;

class LibraryTest {

  @Test
  void catToJsonFile() {
    Main main = new Main("src/main/resources/storage");

    Cat vasyaCat = new Cat("Bayun", 5, true);

    Cat murkaCat = new Cat("Murka", 2, true);
    var nCats = 1;
    List<Cat> cats = new ArrayList<>(nCats);
    cats.add(vasyaCat);
    murkaCat.setKittens(cats);

    try {
      main.serialize(vasyaCat);
      main.serialize(murkaCat);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializer() throws ClassNotFoundException {
    Deserializer deserializer = new Deserializer();
    deserializer.deserialize("src/main/resources/storage/persistence.project.examples.Cat.json");
  }

  @Test
  void animalToJsonFile() {
    Main main;
    main = new Main("src/main/resources/storage");

    Animal someAnimal = new Animal("Murka", 6, true);
    try {
      main.serialize(someAnimal);
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
    Main main = new Main("src/main/resources/storage");

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
      String id = IdGenerator.generateId(animal, ""); //тут указать файл для теста вместо ""
      idList.add(id);
      assertNotNull(id);
      assertFalse(id.isEmpty());
    }

    Set<String> set = new HashSet<>(idList);
    assertEquals(set.size(), idList.size()); //check for unique
  }

  @Test
  public void test() {
    Gson gson = new Gson();
    Animal animal = new Animal("animal", 3, true);
    System.out.println(gson.toJson(animal));
    Cat cat = new Cat("cat", 2, true);
    System.out.println(gson.toJson(cat));
  }
}
