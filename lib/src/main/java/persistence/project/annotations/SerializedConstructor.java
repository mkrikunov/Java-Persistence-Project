package persistence.project.annotations;

import java.lang.annotation.*;

@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedConstructor {
  String returnType();
  String[] parameters() default {};
  String[] parametersTypes() default {};
  String access() default "public";
}