package persistence.project.id;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultIdGenerator implements IdGenerator {
  private final String jsonFilePath = "src/main/resources/data.json";
  private JsonParser jsonParser = new JsonParser();
  private JsonObject jsonObject = new JsonObject();
  private final Gson gson = new Gson();

  private String getCurrId() {
    String id = "";

    try (FileReader fileReader = new FileReader(jsonFilePath)) {
      jsonParser = new JsonParser();
      jsonObject = jsonParser.parse(fileReader).getAsJsonObject();
      id = jsonObject.get("currID").getAsString();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    return id;
  }

  private void setCurrId(int id) {
    try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
      jsonParser = new JsonParser();
      jsonObject = jsonParser.parse(new FileReader(jsonFilePath)).getAsJsonObject();
      jsonObject.addProperty("currID", id);
      try (FileWriter writer = new FileWriter(jsonFilePath)) {
        gson.toJson(jsonObject, writer);
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }


    try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
      fileWriter.write(Integer.toString(id));
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  @Override
  public String generateId(Object object) {
    String id = getCurrId();
    setCurrId(Integer.parseInt(id) + 1);
    return id;
  }
}
