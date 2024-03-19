package persistence.project;

import com.google.gson.annotations.Expose;

public class Wrapper {

  @Expose
  private final Object clazz;
  @Expose
  private final String technicalField = "someValue"; // Техническое поле

  public Wrapper(Object clazz) {
    this.clazz = clazz;
  }

  public Object getClazz() {
    return clazz;
  }
}
