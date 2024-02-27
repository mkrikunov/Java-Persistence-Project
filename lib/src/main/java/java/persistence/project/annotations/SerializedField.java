package java.persistence.project.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedField {
  String name(); // название поля
  Class<?> type(); // тип поля
  String value() default ""; // значение поля
  String access() default "public"; // модификатор
}