package persistence.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Dog;

public class PredicateManagerTest {

  /*
  public PredicateManager makeTwoDogs(String name1, String name2) {
    String storagePath = "src/main/resources/storage";
    Manager manager = new Manager(storagePath);

    Dog dog1 = new Dog(name1, 1, false);
    manager.persist(dog1);

    Dog dog2 = new Dog(name2, 10, true);
    manager.persist(dog2);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    StorageManager storageManager = manager.getStorageManager();
    return new PredicateManager(storageManager.getStorage());
  }

  @Test
  public void filterTest1() {
    PredicateManager predicateManager = makeTwoDogs("Bolik", "Kolik");


    Predicate<JsonElement> predicate = element -> {
      String nameAnimal = predicateManager.searchField(element, "nameAnimal");
      return Objects.equals(nameAnimal, "Kolik");
    };

    JsonArray filteredArray = predicateManager.filter(predicate);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }

  @Test
  public void filterTest2() {
    PredicateManager predicateManager = makeTwoDogs("Barbos", "Taburetka");


    Predicate<JsonElement> predicate = element -> {
      int id = predicateManager.searchID(element);
      return id == 1;
    };

    JsonArray filteredArray = predicateManager.filter(predicate);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }

  @Test
  public void filterNotTest1() {
    PredicateManager predicateManager = makeTwoDogs("Samsa", "Leopold");


    Predicate<JsonElement> predicate = element -> {
      String nameAnimal = predicateManager.searchField(element, "nameAnimal");
      return Objects.equals(nameAnimal, "Kolik");
    };

    JsonArray filteredArray = predicateManager.filterNot(predicate);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }

  @Test
  public void filterNotTest2() {
    PredicateManager predicateManager = makeTwoDogs("Lord", "Kompot");


    Predicate<JsonElement> predicate = element -> {
      int id = predicateManager.searchID(element);
      return id == 1;
    };

    JsonArray filteredArray = predicateManager.filterNot(predicate);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }

  @Test
  public void filterOrTest() {
    PredicateManager predicateManager = makeTwoDogs("Pop", "Gop");


    Predicate<JsonElement> predicate1 = element -> {
      int id = predicateManager.searchID(element);
      return id == 1;
    };

    Predicate<JsonElement> predicate2 = element -> {
      String nameAnimal = predicateManager.searchField(element, "nameAnimal");
      return Objects.equals(nameAnimal, "Leopold");
    };

    Predicate<JsonElement> predicate3 = element -> {
      String nameAnimal = predicateManager.searchField(element, "nameAnimal");
      return Objects.equals(nameAnimal, "Kompot");
    };

    JsonArray filteredArray = predicateManager.filterOr(predicate1, predicate2, predicate3);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }

  @Test
  public void filterAndTest() {
    PredicateManager predicateManager = makeTwoDogs("Rersi", "Tishka");


    Predicate<JsonElement> predicate1 = element -> {
      int id = predicateManager.searchID(element);
      return id == 1;
    };

    Predicate<JsonElement> predicate2 = element -> {
      String nameAnimal = predicateManager.searchField(element, "nameAnimal");
      return Objects.equals(nameAnimal, "Bolik");
    };

    JsonArray filteredArray = predicateManager.filterAnd(predicate1, predicate2);


    // Вывод результатов (просто смотрим, что да как)
    System.out.println("Filtered Array:");
    System.out.println(filteredArray);
  }*/
}
