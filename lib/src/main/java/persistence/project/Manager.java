package persistence.project;

import java.util.HashSet;
import java.util.Set;

public class Manager {

  private final Serializer serializer;
  private final Deserializer deserializer;
  private final Set<Object> managed = new HashSet<>();

  public Manager(String storagePath) {
    StorageManager storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager);
    deserializer = new Deserializer(storageManager);
  }

  public void persist(Object object) {
    managed.add(object);
  }

  public void clear() {
    managed.clear();
  }

  public void remove(Object object) {
    managed.remove(object);
    // + удалить из файла
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
