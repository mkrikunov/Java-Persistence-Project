package persistence.project.examples;

import com.google.gson.annotations.Expose;
import java.util.List;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Cat extends Animal {
  private List<Cat> kittens;
  @Expose
  public String pip = "pip";

  public Cat(String name, int age, boolean pet) {
    super(name, age, pet);
    setKittens(null);
  }

  @Override
  public String toString() {
    return "name: " + getNameAnimal() + "\n" +
        "age: " + getAgeAnimal() + "\n" +
        "pet: " + pet + "\n" +
        "kittens: " + getKittens() + "\n" +
        "pip: " + pip;
  }

  public List<Cat> getKittens() {
    return kittens;
  }

  public void setKittens(List<Cat> kittens) {
    this.kittens = kittens;
  }
}
