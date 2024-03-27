package persistence.project;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import persistence.project.examples.Cat;

public class PredicateManager<T> {
  private final String storagePath;
  private final String fileName;

  public PredicateManager(String storagePath, String fileName) {
    this.storagePath = storagePath;
    this.fileName = fileName;
  }

  public static List<File> findMatchingFiles(String folderPath, String endingString) {
    List<File> matchingFiles = new ArrayList<>();

    File folder = new File(folderPath);
    File[] files = folder.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isFile() && file.getName().endsWith(endingString)) {
          matchingFiles.add(file);
        }
      }
    } else {
      System.out.println("Указанный путь не существует или не является папкой");
    }

    return matchingFiles;
  }

  private Type getType() {
    ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
    return superClass.getActualTypeArguments()[0];
  }

  public List<T> filter(Predicate<T> predicate) throws IOException {
    List<T> filteredRecords = new ArrayList<>();

    String endingString = fileName + ".json";
    List<File> matchingFiles = findMatchingFiles(storagePath, endingString);
    Gson gson = new Gson();

    for (File file : matchingFiles) {
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        JsonElement jsonElement = JsonParser.parseReader(reader);
        if (jsonElement.isJsonArray()) {
          JsonArray jsonArray = jsonElement.getAsJsonArray();
          for (JsonElement element : jsonArray) {
            T record = gson.fromJson(element, getType());
            if (predicate.test(record)) {
              filteredRecords.add(record);
            }
          }
        }
      } catch (IOException e) {
        System.err.println(e.getMessage());
        System.exit(1);
      }
    }

    return filteredRecords;
  }



  public static void main(String[] args) {

    String storagePath = "src/main/resources/storage";

    PredicateManager<Cat> predicateManager = new PredicateManager<>(storagePath, "cat");

    Predicate<Cat> predicate = cat -> cat.getAgeAnimal() == 5;

    try {
      List<Cat> filteredRecords = predicateManager.filter(predicate);
      String dd = "";
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}
