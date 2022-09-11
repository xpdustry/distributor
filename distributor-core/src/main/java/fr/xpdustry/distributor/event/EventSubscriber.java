package fr.xpdustry.distributor.event;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscriber {

  Priority priority() default Priority.NORMAL;

  enum Priority {
    LOWEST, LOW, NORMAL, HIGH, HIGHEST,
  }
}
