package persistence.project.examples;

import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Dog {

  private String nameAnimal;
  private int ageAnimal;
  public boolean pet;

  @ID
  private int id = 0;

  public Dog() {
  }

  public Dog(String name, int age, boolean pet) {
    setNameAnimal(name);
    setAgeAnimal(age);
    this.pet = pet;
  }

  @Override
  public String toString() {
    return "name: " + getNameAnimal() + "\n" +
        "age: " + getAgeAnimal() + "\n" +
        "pet: " + pet;
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
