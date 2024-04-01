package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Dog;

class DogTest {

  @Test
  void dogsTest() {
    String storagePath = "src/main/resources/storage";
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

    Dog dog = manager.retrieve(Dog.class, bobikDog.getId());

    assertThat(dog)
        .usingRecursiveComparison()
        .isEqualTo(bobikDog);

    dog = manager.retrieve(Dog.class, muhtarDog.getId());

    assertThat(dog)
        .usingRecursiveComparison()
        .isEqualTo(muhtarDog);

    dog = new Dog("Dog", 3, false);
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