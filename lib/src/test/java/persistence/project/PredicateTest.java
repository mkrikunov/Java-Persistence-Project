package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Dog;

public class PredicateTest {

  @Test
  public void filterTest() {
    Manager manager = new Manager("src/main/resources/storage");

    Dog bobikDog = new Dog("Bobik", 1, false);
    manager.persist(bobikDog);
    Dog muhtarDog = new Dog("Muhtar", 7, true);
    manager.persist(muhtarDog);
    Dog tolikDog = new Dog("Tolik", 7, true);
    manager.persist(tolikDog);
    Dog leoDog1 = new Dog("Leo", 5, true);
    manager.persist(leoDog1);
    Dog leoDog2 = new Dog("Leo", 7, false);
    manager.persist(leoDog2);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }



    SearchPredicate predicate1 =
        new SearchPredicate("nameAnimal", "Tolik", "==");
    List<Dog> dogs1 = manager.filter(predicate1);

    assertThat(dogs1.get(0))
        .usingRecursiveComparison()
        .isEqualTo(tolikDog);



    SearchPredicate predicate2 =
        new SearchPredicate("nameAnimal", "Leo", "==");
    List<Dog> dogs2 = manager.filter(predicate2);

    List<Dog> dogsList2 = new ArrayList<>();
    dogsList2.add(leoDog1);
    dogsList2.add(leoDog2);

    boolean flag2 = false;
    if (EqualsBuilder.reflectionEquals(dogs2.get(0), dogsList2.get(0), "id")) {
      flag2 = EqualsBuilder.reflectionEquals(dogs2.get(1), dogsList2.get(1), "id");
    } else if (EqualsBuilder.reflectionEquals(dogs2.get(0), dogsList2.get(1), "id")) {
      flag2 = EqualsBuilder.reflectionEquals(dogs2.get(1), dogsList2.get(0), "id");
    }
    Assertions.assertTrue(flag2);



    SearchPredicate predicate3 =
        new SearchPredicate("ageAnimal", 7, "==");
    List<Dog> dogs3 = manager.filterNot(predicate3);

    List<Dog> dogsList3 = new ArrayList<>();
    dogsList3.add(leoDog1);
    dogsList3.add(bobikDog);

    boolean flag3 = false;
    if (EqualsBuilder.reflectionEquals(dogs3.get(0), dogsList3.get(0), "id")) {
      flag3 = EqualsBuilder.reflectionEquals(dogs3.get(1), dogsList3.get(1), "id");
    } else if (EqualsBuilder.reflectionEquals(dogs3.get(0), dogsList3.get(1), "id")) {
      flag3 = EqualsBuilder.reflectionEquals(dogs3.get(1), dogsList3.get(0), "id");
    }
    Assertions.assertTrue(flag3);



    SearchPredicate predicate4 =
        new SearchPredicate("ageAnimal", 5, "<");
    SearchPredicate predicate5 =
        new SearchPredicate("nameAnimal", "Muhtar", "==");
    List<Dog> dogs4 = manager.filterOr(predicate4, predicate5);

    List<Dog> dogsList4 = new ArrayList<>();
    dogsList4.add(bobikDog);
    dogsList4.add(muhtarDog);

    boolean flag4 = false;
    if (EqualsBuilder.reflectionEquals(dogs4.get(0), dogsList4.get(0), "id")) {
      flag4 = EqualsBuilder.reflectionEquals(dogs4.get(1), dogsList4.get(1), "id");
    } else if (EqualsBuilder.reflectionEquals(dogs4.get(0), dogsList4.get(1), "id")) {
      flag4 = EqualsBuilder.reflectionEquals(dogs4.get(1), dogsList4.get(0), "id");
    }
    Assertions.assertTrue(flag4);



    SearchPredicate predicate6 =
        new SearchPredicate("ageAnimal", 5, "<=");
    SearchPredicate predicate7 =
        new SearchPredicate("ageAnimal", 5, ">=");
    SearchPredicate predicate8 =
        new SearchPredicate("nameAnimal", "Leo", "==");
    SearchPredicate predicate9 =
        new SearchPredicate("pet", true, "==");
    List<Dog> dogs5 = manager.filterAnd(predicate6, predicate7, predicate8, predicate9);

    assertThat(dogs5.get(0))
        .usingRecursiveComparison()
        .isEqualTo(leoDog1);
  }

}
