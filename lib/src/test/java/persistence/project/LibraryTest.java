package persistence.project;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import persistence.project.examples.Animal;
import persistence.project.id.*;

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

    @Test void idGeneratorTest() {
      DefaultIdGenerator IdGenerator = new DefaultIdGenerator();
      ArrayList<String> idList = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        String id = IdGenerator.generateId();
        idList.add(id);
        assertNotNull(id);
        assertFalse(id.isEmpty());
      }

      Set<String> set = new HashSet<>(idList);
      assertEquals(set.size(), idList.size()); //check for unique
    }

  @Test
  void someLibraryMethodReturnsTrue() {
    Animal obj = new Animal("Cat");
    Main main = new Main("src/main/resources/data.json");
    main.serialize(obj);
  }
}
