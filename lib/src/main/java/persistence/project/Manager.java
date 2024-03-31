package persistence.project;

import java.util.HashSet;
import java.util.Set;
import persistence.project.id.DefaultIdGenerator;
import persistence.project.id.IdGenerator;

public class Manager {

  private final Serializer serializer;
  private final Deserializer deserializer;
  private final StorageManager storageManager;
  private final Set<Object> managed = new HashSet<>();

  public Manager(String storagePath) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager,
        new DefaultIdGenerator(storageManager));
    deserializer = new Deserializer(storageManager);
  }

  public Manager(String storagePath, IdGenerator idGenerator) {
    storageManager = new StorageManager(storagePath);
    serializer = new Serializer(storageManager, idGenerator);
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
    storageManager.remove(object.getClass().getName(), Utils.getObjectId(object));
  }

  public void flush() throws Exception {
    for (Object object : managed) {
      serializer.createObjectMap(object);
    }
    storageManager.flush();
    clear();
  }

  public Object retrieve(Class<?> targetClazz, int targetId) {
    return deserializer.deserialize(targetClazz, targetId);
  }

  public StorageManager getStorageManager() {
    return this.storageManager;
  }
}
