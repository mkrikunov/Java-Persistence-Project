package persistence.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Horse;
import persistence.project.examples.Tail;

class LibraryTest {

  @Test
  public void demonstrationTest() {
    Manager manager = new Manager("src/main/resources/storage");

    Cat bayun = new Cat("Bayun", 5, true, Tail.Middle);
    manager.persist(bayun);

    Cat seva = new Cat("Seva", 1, true, Tail.Long);
    manager.persist(seva);

    Cat murka = new Cat("Murka", 8, true, Tail.Long);
    manager.persist(murka);
    murka.addKitten(bayun);
    murka.addKitten(seva);
    murka.addOwner("Ivan");
    murka.setMother(new Cat());

    bayun.setMother(murka);
    seva.setMother(murka);
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Cat retrievedCat = manager.retrieve(Cat.class, murka.getId());
    assertThat(retrievedCat)
        .usingRecursiveComparison()
        .isEqualTo(murka);
  }

  @Test
  void test() {
    Manager manager = new Manager("src/main/resources/storage");
    Horse horse = new Horse();
    manager.persist(horse);
    manager.remove(horse);
    manager.remove(horse);
    try {
      manager.flush();
      manager.persist(horse);
      manager.flush();
      manager.remove(horse);
      manager.persist(horse);
      manager.remove(horse);
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
