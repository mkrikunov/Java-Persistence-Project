package persistence.project.id;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultIdGenerator implements IdGenerator{
  private final String fileName = "src/main/resources/currentID.txt";

  private String getCurrId() {
    String id = "";

    try (FileReader fileReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fileReader)) {
      id = reader.readLine().trim();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    return id;
  }

  private void setCurrId(int id) {
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(Integer.toString(id));
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  @Override
  public String generateId() {
    String id = getCurrId();
    setCurrId(Integer.parseInt(id) + 1);
    return id;
  }
}
