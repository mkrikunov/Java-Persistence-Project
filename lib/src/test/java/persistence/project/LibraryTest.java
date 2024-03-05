package persistence.project;

import org.junit.jupiter.api.Test;
import persistence.project.examples.Animal;

class LibraryTest {

  @Test
  void someLibraryMethodReturnsTrue() {
    Animal obj = new Animal("Cat");
    Main main = new Main("src/main/resources/data.json");
    main.serialize(obj);
  }
}
