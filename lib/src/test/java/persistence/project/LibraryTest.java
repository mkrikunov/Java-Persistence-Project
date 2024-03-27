package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.project.Utils.createCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.examples.Horse;

class LibraryTest {

  @Test
  void catToJsonFileAndBack() {
    Manager manager = new Manager("src/main/resources/storage");

    Cat vasyaCat = new Cat("Bayun", 5, true);
    manager.persist(vasyaCat);

    Cat sevaCat = new Cat("Seva", 1, true);
    manager.persist(sevaCat);

    Cat murkaCat = new Cat("Murka", 2, true);
    manager.persist(murkaCat);
    List<Cat> cats = new ArrayList<>();
    cats.add(vasyaCat);
    cats.add(sevaCat);
    murkaCat.setKittens(cats);

    try {
      manager.flush();
    } catch (Exception e) {
      System.err.println("Error while flushing");
      throw new RuntimeException(e);
    }

    Cat cat = (Cat) manager.retrieve(Cat.class, 3);

    assertThat(cat).usingRecursiveComparison().isEqualTo(murkaCat);
  }

  @Test
  void catToJsonFile() {
    Serializer serializer = new Serializer("src/main/resources/storage");

    Cat vasyaCat = new Cat("Bayun", 5, true);
    Cat sevaCat = new Cat("Seva", 1, true);

    Cat murkaCat = new Cat("Murka", 2, true);
    List<Cat> cats = new ArrayList<>();
    cats.add(vasyaCat);
    cats.add(sevaCat);
    murkaCat.setKittens(cats);

    try {
      serializer.serialize(vasyaCat);
      serializer.serialize(murkaCat);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializeCatMurka() {
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    Cat cat = (Cat) deserializer.deserialize(Cat.class, 3);
    System.out.println(cat);
  }

  @Test
  void dogBobikToJsonFile() {
    Serializer serializer;
    serializer = new Serializer("src/main/resources/storage");

    Dog bobikDog = new Dog("Bobik", 1, false);
    try {
      serializer.serialize(bobikDog);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dogMuhtarToJsonFile() {
    Serializer serializer = new Serializer("src/main/resources/storage");

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    try {
      serializer.serialize(muhtarDog);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void horseJuliusToJson() {
    Serializer serializer = new Serializer("src/main/resources/storage");

    Horse julius = new Horse("Julius", 5, true);

    Horse daphne = new Horse("Daphne", 4, false);

    julius.setSpouse(daphne);
    try {
      serializer.serialize(julius);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializeHorseDaphne() {
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    Horse horse = (Horse) deserializer.deserialize(Horse.class, 1);
    System.out.println(horse);
    assert horse.getNameAnimal().equals("Daphne");
    assert horse.getAgeAnimal() == 4;
    assert !horse.pet;
  }

  @Test
  void deserializeHorseJulius() {
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    Horse horse = (Horse) deserializer.deserialize(Horse.class, 2);
    System.out.println(horse);
    assert horse.getId() == 2;
  }

  @Test
  void testCreateCollection() {
    Class<?> fieldType = List.class;
    Collection<Object> list = createCollection(fieldType);
    list.add(new Object());
    System.out.println(list.getClass().getName());
    fieldType = Set.class;
    System.out.println(createCollection(fieldType).getClass().getName());
  }
}
