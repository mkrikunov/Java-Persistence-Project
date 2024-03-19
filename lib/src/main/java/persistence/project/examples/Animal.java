package persistence.project.examples;

import com.google.gson.annotations.Expose;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Animal {

  @Expose
  private String nameAnimal;
  @Expose
  private int ageAnimal;
  @Expose
  public boolean pet;
  @Expose
  @ID
  private int id;

  public Animal(String name, int age, boolean pet) {
    setNameAnimal(name);
    setAgeAnimal(age);
    this.pet = pet;
  }

  public String getNameAnimal() {
    return nameAnimal;
  }

  public int getAgeAnimal() {
    return ageAnimal;
  }

  public void setNameAnimal(String nameAnimal) {
    this.nameAnimal = nameAnimal;
  }

  public void setAgeAnimal(int ageAnimal) {
    this.ageAnimal = ageAnimal;
  }
}
