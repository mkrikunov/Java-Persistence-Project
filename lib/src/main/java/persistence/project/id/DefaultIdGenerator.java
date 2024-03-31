package persistence.project.id;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import persistence.project.StorageManager;

public class DefaultIdGenerator implements IdGenerator {
  private final StorageManager storageManager;

  public DefaultIdGenerator(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  private String getCurrId(String className) {
    String id = "";

    JsonArray jsonArray = storageManager.getJsonArrayByClassName(className);
    if (!jsonArray.isJsonNull() && !jsonArray.isEmpty()) {
      JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
      if (jsonObject != null && jsonObject.has("currID")) {
        id = jsonObject.get("currID").getAsString();
      }
    }

    setCurrId(Integer.parseInt(id) + 1, jsonArray);
    return id;
  }

  private void setCurrId(int newCurrID, JsonArray jsonArray) {

    JsonObject firstElement = jsonArray.get(0).getAsJsonObject();
    firstElement.addProperty("currID", newCurrID);
    jsonArray.set(0, firstElement);
  }

  @Override
  public int generateId(Object object, String className) {

    int id = Integer.parseInt(getCurrId(className));

    try {
      Class<?> objectClass = object.getClass();
      Field idField = objectClass.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(object, id);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return id;
  }
}
