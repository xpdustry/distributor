package fr.xpdustry.distributor.event;

import java.util.*;

public interface EventBus {

  static EventBus mindustry() {
    return MindustryEventBus.INSTANCE;
  }

  <E> void post(final E event);

  <E> void register(final Class<E> event, final MonoEventListener<E> listener);

  void register(final EventListener listener);

  <E> void unregister(final Class<E> event, final MonoEventListener<E> listener);

  void unregister(final EventListener listener);
}
