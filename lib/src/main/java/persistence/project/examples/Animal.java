package persistence.project.examples;

import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Animal {
  private final String nameAnimal;
  private int ageAnimal;

  @ID
  private int id;
  public Animal(String name, int age) {
    this.nameAnimal = name;
    this.ageAnimal = age;
  }

  public String getNameAnimal() {
    return nameAnimal;
  }

  public int getAgeAnimal() {
    return ageAnimal;
  }
  public void setAgeAnimal(int newAge) {
    this.ageAnimal = newAge;
  }
}
