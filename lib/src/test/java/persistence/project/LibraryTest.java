package persistence.project;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Animal;

class LibraryTest {
    @Test void someLibraryMethodReturnsTrue() {
        Animal obj = new Animal("Cat");
      Main main;
      try {
        main = new Main("src/main/resources/data.json");
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      main.serialize(obj);
    }
}
