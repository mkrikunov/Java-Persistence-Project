package persistence.project.examples;

import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Animal {

  private final String nameAnimal;
  private final int ageAnimal;
  public boolean pet;

  public Animal(String name, int age, boolean pet) {
    this.nameAnimal = name;
    this.ageAnimal = age;
    this.pet = pet;
  }

  public String getNameAnimal() {
    return nameAnimal;
  }

  public int getAgeAnimal() {
    return ageAnimal;
  }
}
