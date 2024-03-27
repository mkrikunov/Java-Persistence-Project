package persistence.project;

import java.util.ArrayList;
import java.util.List;

public class Manager {
  private final Serializer serializer;
  private final Deserializer deserializer;
  private final List<Object> managed = new ArrayList<>();
  public Manager(String storagePath) {
    serializer = new Serializer(storagePath);
    deserializer = new Deserializer(storagePath);
  }
  public void persist(Object object) {
    managed.add(object);
  }

  public void flush() throws Exception {
    for (Object object : managed) {
      serializer.serialize(object);
    }
    managed.clear();
  }

  public Object retrieve(Class<?> targetClazz, int targetId) {
    return deserializer.deserialize(targetClazz, targetId);
  }
}
