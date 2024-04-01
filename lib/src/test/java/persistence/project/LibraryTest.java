package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.examples.Horse;

class LibraryTest {

  private final String storagePath = "src/main/resources/storage";

  @Test
  void catToJsonFileAndBack() {
    Manager manager = new Manager(storagePath);

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

    Cat cat = (Cat) manager.retrieve(Cat.class, murkaCat.getId());

    assertThat(murkaCat)
        .usingRecursiveComparison()
        .isEqualTo(cat);
  }

  @Test
  void dogsToJsonFile() {
    Manager manager = new Manager(storagePath);

    Dog bobikDog = new Dog("Bobik", 1, false);
    manager.persist(bobikDog);

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    manager.persist(muhtarDog);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Dog dog = (Dog) manager.retrieve(Dog.class, bobikDog.getId());

    assertThat(dog)
        .usingRecursiveComparison()
        .isEqualTo(bobikDog);

    dog = (Dog) manager.retrieve(Dog.class, muhtarDog.getId());

    assertThat(dog)
        .usingRecursiveComparison()
        .isEqualTo(muhtarDog);
  }

  @Test
  public void horseToJsonFileAndBack() {
    Manager manager = new Manager(storagePath);

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

    Horse horse1 = (Horse) manager.retrieve(Horse.class, daphne.getId());
    assertThat(horse1)
        .usingRecursiveComparison()
        .isEqualTo(daphne);

    Horse horse2 = (Horse) manager.retrieve(Horse.class, julius.getId());
    assertThat(horse2)
        .usingRecursiveComparison()
        .isEqualTo(julius);
  }

  @Test
  public void serializeAndRemoveDog() {
    Manager manager = new Manager(storagePath);
    Dog dog = new Dog("Dog", 3, false);
    manager.persist(dog);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    assertThat(manager.retrieve(Dog.class, dog.getId()))
        .usingRecursiveComparison()
        .isEqualTo(dog);


    manager.remove(dog);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    assertThat(manager.retrieve(Dog.class, dog.getId()))
        .isNull();
  }
}
