package persistence.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.function.Predicate;

public class SearchPredicate {

  private final Predicate<JsonElement> predicate;

  public SearchPredicate(String name, Integer value, String operation) {
    if (Objects.equals(name, "id")) {
      predicate = element -> {
        int id = searchID(element);
        return switch (operation) {
          case (">") -> id > value;
          case ("<") -> id < value;
          case ("==") -> id == value;
          default -> {
            System.out.println("Unknown operation");
            yield false;
          }
        };
      };
    } else {
      predicate = element -> {
        String field = searchField(element, name);
        if (isParsable(field)) {
          int num = Integer.parseInt(field);
          return switch (operation) {
            case (">") -> num > value;
            case ("<") -> num < value;
            case ("==") -> num == value;
            default -> {
              System.out.println("Unknown operation");
              yield false;
            }
          };
        } else return false;
      };
    }
  }

  public Predicate<JsonElement> getPredicate() {
    return this.predicate;
  }

  public SearchPredicate(String name, String value, String operation) {
    predicate = element -> {
      String field = searchField(element, name);
      if (Objects.equals(operation, "==")) {
        return Objects.equals(field, value);
      } else {
        System.out.println("Unknown operation");
        return false;
      }
    };
  }

  private static boolean isParsable(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String searchField(JsonElement element, String fieldName) {
    JsonArray fieldsArray = element.getAsJsonObject().get("fields").getAsJsonArray();
    String fieldValue = "";
    for (JsonElement elem: fieldsArray) {
      fieldValue = elem.getAsJsonObject().get(fieldName).getAsString();
      if (!fieldValue.isEmpty()) break;
    }
    return fieldValue;
  }

  private int searchID(JsonElement element) {
    return element.getAsJsonObject().get("id").getAsInt();
  }
}
