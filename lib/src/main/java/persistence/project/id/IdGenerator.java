package persistence.project.id;

public interface IdGenerator {
  public int generateId(Object object, String jsonFilePath);
}
