package persistence.project;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Dog;
import persistence.project.examples.Horse;

class LibraryTest {

  @Test
  void catToJsonFileAndBack() {
    Main main = new Main("src/main/resources/storage");

    Cat vasyaCat = new Cat("Bayun", 5, true);
    Cat sevaCat = new Cat("Seva", 1, true);

    Cat murkaCat = new Cat("Murka", 2, true);
    List<Cat> cats = new ArrayList<>();
    cats.add(vasyaCat);
    cats.add(sevaCat);
    murkaCat.setKittens(cats);

    try {
      main.serialize(vasyaCat);
      main.serialize(murkaCat);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Cat cat = new Cat();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(cat, 3);
    assert cat.getNameAnimal().equals(murkaCat.getNameAnimal());
    assert cat.getAgeAnimal() == murkaCat.getAgeAnimal();
    assert cat.pet == murkaCat.pet;
    var nKittens = cat.getKittens().size();
    assert nKittens == murkaCat.getKittens().size();
    List<Cat> kittensCat = cat.getKittens();
    List<Cat> kittensMurka = murkaCat.getKittens();
    for (int i = 0; i < nKittens; i++) {
      assert kittensCat.get(i).getNameAnimal().equals(kittensMurka.get(i).getNameAnimal());
    }
  }

  @Test
  void catToJsonFile() {
    Main main = new Main("src/main/resources/storage");

    Cat vasyaCat = new Cat("Bayun", 5, true);
    Cat sevaCat = new Cat("Seva", 1, true);

    Cat murkaCat = new Cat("Murka", 2, true);
    List<Cat> cats = new ArrayList<>();
    cats.add(vasyaCat);
    cats.add(sevaCat);
    murkaCat.setKittens(cats);

    try {
      main.serialize(vasyaCat);
      main.serialize(murkaCat);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializeCatMurka() {
    Cat cat = new Cat();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(cat, 3);
    System.out.println(cat);
  }

  @Test
  void dogBobikToJsonFile() {
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
  void dogMuhtarToJsonFile() {
    Main main = new Main("src/main/resources/storage");

    Dog muhtarDog = new Dog("Muhtar", 10, true);
    try {
      main.serialize(muhtarDog);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void horseJuliusToJson() {
    Main main = new Main("src/main/resources/storage");

    Horse julius = new Horse("Julius", 5, true);

    Horse daphne = new Horse("Daphne", 4, false);

    julius.setSpouse(daphne);
    try {
      main.serialize(julius);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deserializeHorseDaphne() {
    Horse horse = new Horse();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(horse, 1);
    System.out.println(horse);
    assert horse.getNameAnimal().equals("Daphne");
    assert horse.getAgeAnimal() == 4;
    assert !horse.pet;
  }

  @Test
  void deserializeHorseJulius() {
    Horse horse = new Horse();
    Deserializer deserializer = new Deserializer("src/main/resources/storage");
    deserializer.deserialize(horse, 2);
    System.out.println(horse);
  }
}
