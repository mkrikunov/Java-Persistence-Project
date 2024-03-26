package persistence.project.examples;

import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Horse {

  private String nameAnimal;
  private int ageAnimal;
  public boolean pet;

  @ID
  private int id = 0;
  private Horse spouse;

  public Horse() {
  }

  public void setSpouse(Horse spouse) {
    this.spouse = spouse;
  }

  public Horse getSpouse() {
    return spouse;
  }

  @Override
  public String toString() {
    var str = "name: " + getNameAnimal() + "\n" +
        "age: " + getAgeAnimal() + "\n" +
        "pet: " + pet + "\n";
    if (getSpouse() != null) {
      str += "spouse: " + spouse.getNameAnimal();
    }
    return str;
  }

  public int getAgeAnimal() {
    return ageAnimal;
  }

  public void setAgeAnimal(int ageAnimal) {
    this.ageAnimal = ageAnimal;
  }

  public String getNameAnimal() {
    return nameAnimal;
  }

  public void setNameAnimal(String nameAnimal) {
    this.nameAnimal = nameAnimal;
  }
}
