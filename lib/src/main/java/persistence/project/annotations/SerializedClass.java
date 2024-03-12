package persistence.project.annotations;

import java.lang.annotation.*;
import persistence.project.id.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedClass {
  //Class<? extends DefaultIdGenerator> idGenerator() default DefaultIdGenerator.class;
}