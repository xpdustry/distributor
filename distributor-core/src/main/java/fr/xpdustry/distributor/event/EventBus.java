package fr.xpdustry.distributor.event;

import java.util.*;
import java.util.function.*;

public interface EventBus {

  static EventBus mindustry() {
    return MindustryEventBus.INSTANCE;
  }

  <E> void post(final E event);

  void register(final EventListener listener);

  <E> void register(final Class<E> event, final EventPriority priority, final Consumer<E> listener);

  default <E> void register(final Class<E> event, final Consumer<E> listener) {
    register(event, EventPriority.LAST, listener);
  }

  <E> void unregister(final Class<E> event, final Consumer<E> listener);

  void unregister(final EventListener listener);
}
