package fr.xpdustry.distributor.event;

import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractEventBus implements EventBus {

  @SuppressWarnings("rawtypes")
  private final Map<Object, List<ObjectMethodEventListener>> objects = new HashMap<>();

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void register(final EventListener listener) {
    if (objects.containsKey(listener)) {
      return;
    }
    final List<ObjectMethodEventListener> handlers = new ArrayList<>();
    for (final var method : listener.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(MethodEventListener.class);
      if (annotation == null) {
        continue;
      }
      if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
      }
      if (method.getReturnType() != void.class) {
        throw new IllegalArgumentException("The event handler on " + method + " doesn't return void.");
      }
      final var clazz = method.getParameterTypes()[0];
      final var handler = new ObjectMethodEventListener(clazz, listener, method);
      handlers.add(handler);
      register(clazz, handler);
    }
    objects.put(listener, handlers.isEmpty() ? Collections.emptyList() : handlers);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void unregister(Object listener) {
    final var listeners = objects.remove(listener);
    if (listeners != null) {
      listeners.forEach(l -> unregister(l.clazz, l));
    }
  }

  private static final class ObjectMethodEventListener<E> implements MonoEventListener<E> {

    private final Class<E> clazz;
    private final Object object;
    private final Method method;

    ObjectMethodEventListener(final Class<E> clazz, final Object object, final Method method) {
      this.clazz = clazz;
      this.object = object;
      this.method = method;
    }

    @Override
    public void onEvent(final E event) {
      try {
        method.invoke(object, event);
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
