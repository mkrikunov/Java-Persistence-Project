package persistence.project.examples;

import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Dog extends Animal {

  public Dog(String name, int age, boolean pet) {
    super(name, age, pet);
  }
}
