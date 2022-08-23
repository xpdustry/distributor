package fr.xpdustry.distributor.event;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public abstract class AbstractEventBus implements EventBus {

  @SuppressWarnings("rawtypes")
  private final Map<EventListener, List<MethodEventListener>> objects = new HashMap<>();

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void register(final EventListener listener) {
    if (objects.containsKey(listener)) {
      return;
    }

    final List<MethodEventListener> handlers = new ArrayList<>();
    for (final var method : listener.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(EventHandler.class);
      if (annotation == null) {
        continue;
      } else if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
      }

      final var handler = new MethodEventListener(method.getParameterTypes()[0], listener, method);
      handlers.add(handler);
      register(handler.clazz, annotation.priority(), handler);
    }

    objects.put(listener, handlers.isEmpty() ? Collections.emptyList() : handlers);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void unregister(final EventListener listener) {
    final var listeners = objects.remove(listener);
    if (listeners != null) {
      listeners.forEach(l -> unregister(l.clazz, l));
    }
  }

  private static final class MethodEventListener<E> implements Consumer<E> {

    private final Class<E> clazz;
    private final Object object;
    private final Method method;

    MethodEventListener(final Class<E> clazz, final Object object, final Method method) {
      this.clazz = clazz;
      this.object = object;
      this.method = method;
    }

    @Override
    public void accept(final E event) {
      try {
        method.invoke(object, event);
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
