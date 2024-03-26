package persistence.project;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.examples.Horse;

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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializer() {
    Cat cat = new Cat();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(cat, "2");
    System.out.println(cat);
  }

  @Test
  void dogToJsonFile() {
    Main main;
    main = new Main("src/main/resources/storage");

    Dog bobikDog = new Dog("Bobik", 1, false);
    try {
      main.serialize(bobikDog);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dogToJsonFile2() {
    Main main = new Main("src/main/resources/storage");

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    try {
      main.serialize(muhtarDog);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
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
  */
  @Test
  public void horseToJson() {
    Main main = new Main("src/main/resources/storage");

    Horse horse = new Horse();
    horse.setNameAnimal("Angel");
    horse.setAgeAnimal(5);

    Horse horseSpouse = new Horse();
    horseSpouse.setNameAnimal("Angel's spouse");

    horse.setSpouse(horseSpouse);
    try {
      main.serialize(horse);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializeHorse() {
    Horse horse = new Horse();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(horse, "1");
    System.out.println(horse);
  }
}
