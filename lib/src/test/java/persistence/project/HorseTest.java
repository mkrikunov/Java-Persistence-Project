package persistence.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Horse;

class HorseTest {

  @Test
  public void horsesTest() {
    String storagePath = "src/main/resources/storage";
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

    Horse horse1 = manager.retrieve(Horse.class, daphne.getId());
    assertThat(horse1)
        .usingRecursiveComparison()
        .isEqualTo(daphne);

    Horse horse2 = manager.retrieve(Horse.class, julius.getId());
    assertThat(horse2)
        .usingRecursiveComparison()
        .isEqualTo(julius);
  }
}
