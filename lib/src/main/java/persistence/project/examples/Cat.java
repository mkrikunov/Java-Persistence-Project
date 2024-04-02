package persistence.project.examples;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
  private Cat mother;
  public Set<String> owners;

  public Cat() {
  }

  public Cat(String name, int age, boolean pet, Tail tail) {
    setNameAnimal(name);
    setAgeAnimal(age);
    this.pet = pet;
    this.tail = tail;
  }

  @Override
  public String toString() {
    return "name: " + getNameAnimal() + "\n" +
        "age: " + getAgeAnimal() + "\n" +
        "pet: " + pet + "\n" +
        "kittens: " + getKittens() + "\n" +
        "tail: " + tail + "\n" +
        "mother: " + getMother().getNameAnimal() + "\n" +
        "owners: " + getOwners();
  }

  public List<Cat> getKittens() {
    return kittens;
  }

  public void addKitten(Cat kitten) {
    if (kittens == null) {
      kittens = new ArrayList<>();
    }
    kittens.add(kitten);
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

  public Cat getMother() {
    return mother;
  }

  public void setMother(Cat mother) {
    this.mother = mother;
  }

  public void addOwner(String ownerName) {
    if (owners == null) {
      owners = new HashSet<>();
    }
    owners.add(ownerName);
  }

  public Set<String> getOwners() {
    return owners;
  }
}
