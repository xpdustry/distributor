package fr.xpdustry.distributor.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface EventBus {

  @SuppressWarnings({"rawtypes", "unchecked"})
  default List<EventListener<?>> register(final Object listener) {
    // TODO It's temporary, need support for enum events
    final List<EventListener<?>> listeners = new ArrayList<>();
    for (final var method : listener.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(EventHandler.class);
      if (annotation == null) {
        continue;
      }
      if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count");
      }
      final var event = method.getParameterTypes()[0];
      final var handler = new MethodEventListener(listener, method);
      listeners.add(handler);
      register(event, annotation.async(), handler);
    }
    return Collections.unmodifiableList(listeners);
  }

  <E> void register(final Class<E> event, final boolean async, final EventListener<E> listener);

  default <E> void register(final Class<E> event, final EventListener<E> listener) {
    register(event, false, listener);
  }

  <E extends Enum<E>> void register(final Enum<E> event, final boolean async, final EventListener<E> listener);

  default <E extends Enum<E>> void register(final Enum<E> event, final EventListener<E> listener) {
    register(event, false, listener);
  }

  void post(final Object event);

  void unregister(final EventListener<?> listener);
}
