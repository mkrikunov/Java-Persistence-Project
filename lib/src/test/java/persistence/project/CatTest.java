package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Cat;
import persistence.project.examples.Tail;

class CatTest {

  @Test
  void catsTest() {
    // Сериализуем
    String storagePath = "src/main/resources/storage";
    Manager manager = new Manager(storagePath);

    Cat bayunCat = new Cat("Bayun", 5, true, Tail.Short);
    manager.persist(bayunCat);

    Cat sevaCat = new Cat("Seva", 1, true, Tail.Long);
    manager.persist(sevaCat);

    Cat murkaCat = new Cat("Murka", 2, true, Tail.Middle);
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

    // Десериализуем
    Cat cat = manager.retrieve(Cat.class, murkaCat.getId());

    assertThat(murkaCat)
        .usingRecursiveComparison()
        .isEqualTo(cat);

    // Меняем хвост с Long на Short
    manager.persist(sevaCat);
    sevaCat.tail = Tail.Short;
    try {
      manager.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    Cat cat1 = manager.retrieve(Cat.class, sevaCat.getId());
    assertThat(cat1.tail)
        .isEqualTo(Tail.Short);
  }
}
