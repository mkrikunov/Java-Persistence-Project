package persistence.project.examples;

import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Horse extends Animal {
  private Horse spouse;
  public Horse() {
  }

  @Override
  public String toString() {
    //return super.toString() + "\n" + "spouse: " + spouse.getNameAnimal();
    return super.toString();
  }

  public void setSpouse(Horse spouse) {
    this.spouse = spouse;
  }

  public Horse getSpouse() {
    return spouse;
  }
}
