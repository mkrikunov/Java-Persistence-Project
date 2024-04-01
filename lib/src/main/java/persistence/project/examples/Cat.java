package persistence.project.examples;

import java.util.List;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Cat {

  private String nameAnimal;
  private int ageAnimal;
  public boolean pet;
  public Tail tail;

  @ID
  private int id = 0;

  private List<Cat> kittens;

  public Cat() {
  }

  public Cat(String name, int age, boolean pet, Tail tail) {
    setNameAnimal(name);
    setAgeAnimal(age);
    this.pet = pet;
    this.tail = tail;
    setKittens(null);
  }

  @Override
  public String toString() {
    return "name: " + getNameAnimal() + "\n" +
        "age: " + getAgeAnimal() + "\n" +
        "pet: " + pet + "\n" +
        getKittensToString() +
        "tail: " + tail;
  }

  public String getKittensToString() {
    if (kittens == null) {
      return "";
    }
    StringBuilder string = new StringBuilder("kittens: ");
    for (Cat cat : getKittens()) {
      string.append(cat.getNameAnimal()).append(" ");
    }
    return string + "\n";
  }

  public List<Cat> getKittens() {
    return kittens;
  }

  public void setKittens(List<Cat> kittens) {
    this.kittens = kittens;
  }

  public String getNameAnimal() {
    return nameAnimal;
  }

  public void setNameAnimal(String nameAnimal) {
    this.nameAnimal = nameAnimal;
  }

  public int getAgeAnimal() {
    return ageAnimal;
  }

  public void setAgeAnimal(int ageAnimal) {
    this.ageAnimal = ageAnimal;
  }

  public int getId() {
    return id;
  }
}
