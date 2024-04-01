package persistence.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Chicken;

class ChickenTest {

  @Test
  void chickenTest() {
    Chicken chicken = new Chicken();
    Manager manager = new Manager("src/main/resources/storage");
    manager.persist(chicken);
    chicken.addLaidEggsAtTime(15);
    try {
      manager.flush();

      chicken.addLaidEggsAtTime(18);
      chicken.addLaidEggsAtTime(20);
      manager.persist(chicken);
      manager.flush();

      Chicken anotherChicken = manager.retrieve(Chicken.class, chicken.getId());
      assertThat(anotherChicken.getLaidEggsAtTime())
          .isEqualTo(chicken.getLaidEggsAtTime());

      chicken.setName("Klusha");
      chicken.addOwner("Petr");
      chicken.addOwner("Natasha");
      manager.persist(chicken);
      manager.flush();

      assertThat(manager.retrieve(Chicken.class, chicken.getId()))
          .usingRecursiveComparison()
          .isEqualTo(chicken);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
