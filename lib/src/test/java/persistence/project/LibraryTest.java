package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.examples.Horse;

class LibraryTest {

  @Test
  void catToJsonFileAndBack() {
    Manager manager = new Manager("src/main/resources/storage");

    Cat bayunCat = new Cat("Bayun", 5, true);
    manager.persist(bayunCat);

    Cat sevaCat = new Cat("Seva", 1, true);
    manager.persist(sevaCat);

    Cat murkaCat = new Cat("Murka", 2, true);
    manager.persist(murkaCat);
    List<Cat> cats = new ArrayList<>();
    cats.add(bayunCat);
    cats.add(sevaCat);
    murkaCat.setKittens(cats);

    try {
      manager.flush();
    } catch (Exception e) {
      System.err.println("Error while flushing");
      throw new RuntimeException(e);
    }

    Cat cat = (Cat) manager.retrieve(Cat.class, 3);

    assertThat(murkaCat)
        .usingRecursiveComparison()
        .ignoringFields("id", "kittens.id")
        .isEqualTo(cat);
  }

  @Test
  void dogsToJsonFile() {
    Manager manager = new Manager("src/main/resources/storage");

    Dog bobikDog = new Dog("Bobik", 1, false);
    manager.persist(bobikDog);

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    manager.persist(muhtarDog);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void horseToJsonFileAndBack() {
    Manager manager = new Manager("src/main/resources/storage");

    Horse julius = new Horse("Julius", 5, true);
    Horse daphne = new Horse("Daphne", 4, false);
    julius.setSpouse(daphne);

    manager.persist(julius);
    try {
      manager.flush();
    } catch (Exception e) {
      System.err.println("Error while flushing");
      throw new RuntimeException(e);
    }

    Horse horse1 = (Horse) manager.retrieve(Horse.class, 1);
    assertThat(horse1).usingRecursiveComparison().isEqualTo(daphne);

    Horse horse2 = (Horse) manager.retrieve(Horse.class, 2);
    assertThat(horse2).usingRecursiveComparison().isEqualTo(julius);
  }
}
