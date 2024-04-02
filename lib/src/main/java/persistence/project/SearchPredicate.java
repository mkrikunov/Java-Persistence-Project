package persistence.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.function.Predicate;

public class SearchPredicate {

  private final Predicate<JsonElement> predicate;

  /**
   * Конструктор для предиката с числовым значением.
   *
   * @param name      - имя поля
   * @param value     - сравниваемое значение
   * @param operation - операция сравнения значения поля с именем name со сравниваемым значением
   */
  public SearchPredicate(String name, Integer value, String operation) {
    if (Objects.equals(name, "id")) {
      predicate = element -> {
        int id = searchID(element);
        return switch (operation) {
          case (">") -> id > value;
          case (">=") -> id >= value;
          case ("<") -> id < value;
          case ("<=") -> id <= value;
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
            case (">=") -> num >= value;
            case ("<") -> num < value;
            case ("<=") -> num <= value;
            case ("==") -> num == value;
            default -> {
              System.out.println("Unknown operation");
              yield false;
            }
          };
        } else {
          return false;
        }
      };
    }
  }

  /**
   * Конструктор для предиката со строковым значением.
   *
   * @param name      - имя поля
   * @param value     - сравниваемое значение
   * @param operation - операция сравнения значения поля с именем name со сравниваемым значением
   */
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

  /**
   * Конструктор для предиката с булевым значением.
   *
   * @param name      - имя поля
   * @param value     - сравниваемое значение
   * @param operation - операция сравнения значения поля с именем name со сравниваемым значением
   */
  public SearchPredicate(String name, boolean value, String operation) {
    predicate = element -> {
      boolean field = searchBooleanField(element, name);
      if (Objects.equals(operation, "==")) {
        return field == value;
      } else {
        System.out.println("Unknown operation");
        return false;
      }
    };
  }

  public Predicate<JsonElement> getPredicate() {
    return this.predicate;
  }

  private static boolean isParsable(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean searchBooleanField(JsonElement element, String fieldName) {
    JsonArray fieldsArray = element.getAsJsonObject().get("fields").getAsJsonArray();
    boolean fieldValue = false;
    for (JsonElement elem : fieldsArray) {
      try {
        fieldValue = elem.getAsJsonObject().get(fieldName).getAsBoolean();
      } catch (NullPointerException e) {
        continue;
      }
      if (!fieldValue) {
        break;
      }
    }
    return fieldValue;
  }

  private String searchField(JsonElement element, String fieldName) {
    JsonArray fieldsArray = element.getAsJsonObject().get("fields").getAsJsonArray();
    String fieldValue = "";
    for (JsonElement elem : fieldsArray) {
      try {
        fieldValue = elem.getAsJsonObject().get(fieldName).getAsString();
      } catch (NullPointerException e) {
        continue;
      }
      if (!fieldValue.isEmpty()) {
        break;
      }
    }
    return fieldValue;
  }

  private int searchID(JsonElement element) {
    return element.getAsJsonObject().get("id").getAsInt();
  }
}
