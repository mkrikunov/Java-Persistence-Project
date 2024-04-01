package persistence.project.id;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import persistence.project.StorageManager;

public class DefaultIdGenerator implements IdGenerator {

  private final StorageManager storageManager;

  public DefaultIdGenerator(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  private void setCurrId(int newCurrID, JsonArray jsonArray) {
    JsonObject firstElement = jsonArray.get(0).getAsJsonObject();
    firstElement.addProperty("currID", newCurrID);
    jsonArray.set(0, firstElement);
  }

  @Override
  public int generateId(String className) {
    int id = 0;
    JsonArray jsonArray = storageManager.getJsonArrayByClassName(className);
    if (!jsonArray.isJsonNull() && !jsonArray.isEmpty()) {
      JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
      if (jsonObject != null && jsonObject.has("currID")) {
        id = jsonObject.get("currID").getAsInt();
      }
    }
    setCurrId(id + 1, jsonArray);
    return id;
  }
}
