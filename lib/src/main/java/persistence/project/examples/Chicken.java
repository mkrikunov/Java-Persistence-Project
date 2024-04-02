package persistence.project.examples;

import java.util.ArrayList;
import java.util.List;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;

@SerializedClass
public class Chicken {

  @ID
  private int id = 0;
  private final List<Integer> laidEggsAtTime = new ArrayList<>();
  private final List<String> owners = new ArrayList<>();
  private String name = "";

  public Chicken() {

  }

  public List<Integer> getLaidEggsAtTime() {
    return laidEggsAtTime;
  }

  public void addLaidEggsAtTime(Integer count) {
    laidEggsAtTime.add(count);
  }

  public int getId() {
    return id;
  }

  public List<String> getOwners() {
    return owners;
  }

  public void addOwner(String newOwner) {
    owners.add(newOwner);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
