package fr.xpdustry.distributor.event;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

  EventPriority priority() default EventPriority.LAST;
}


