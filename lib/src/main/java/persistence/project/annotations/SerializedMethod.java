package persistence.project.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedMethod {
  String name(); // Название метода
  String returnType();
  String[] parameters() default {};
  String[] parametersTypes() default {};
  String access() default "public";
}