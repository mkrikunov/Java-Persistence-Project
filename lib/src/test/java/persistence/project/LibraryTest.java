package persistence.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Tail;

class LibraryTest {

  private void createCats(Manager manager) {
    for (int i = 0; i < 500; i++) {
      manager.persist(new Cat("cat", 10, false, Tail.Middle));
    }
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

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

    createCats(manager);

    SearchPredicate predicate = new SearchPredicate("nameAnimal", "cat", "==");
    assert manager.filter(predicate).size() == 500;
  }
}
