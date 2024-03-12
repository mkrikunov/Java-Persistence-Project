package persistence.project.examples;

import java.util.ArrayList;
import java.util.List;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Cat extends Animal {
  private final List<Cat> kittens;
  public String pip = "pip";

  public Cat(String name, int age, boolean pet) {
    super(name, age, pet);
    kittens = new ArrayList<>();
  }

  public List<Cat> getKittens() {
    return kittens;
  }
}
