package fr.xpdustry.distributor.event;

import java.lang.reflect.Method;

// TODO Make a better class :)
final class MethodEventListener<E> implements EventListener<E> {

  private final Object listener;
  private final Method method;

  MethodEventListener(final Object listener, final Method method) {
    this.listener = listener;
    this.method = method;
  }

  @Override
  public void onEvent(final E event) {
    try {
      method.invoke(listener, event);
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
