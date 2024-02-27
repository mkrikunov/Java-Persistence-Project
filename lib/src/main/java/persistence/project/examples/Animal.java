package persistence.project.examples;

public class Animal {
  //@SerializedField(name = "name", type = String)
  private final String name;
  public Animal(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
