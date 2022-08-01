package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import java.util.*;

@SuppressWarnings("unchecked")
final class MindustryEventBus extends AbstractEventBus {

  static final MindustryEventBus INSTANCE = new MindustryEventBus();

  @SuppressWarnings("rawtypes")
  private final Map<EventListener, EventListenerConsWrapper> wrappers = new HashMap<>();

  private MindustryEventBus() {
  }

  @Override
  public <E> void post(final E event) {
    Events.fire(event);
  }

  @Override
  public <E> void register(final Class<E> event, final EventListener<E> listener) {
    final var wrapper = new EventListenerConsWrapper<>(listener);
    Events.on(event, wrapper);
    wrappers.put(listener, wrapper);
  }

  @Override
  public <E> void unregister(final Class<E> event, final EventListener<E> listener) {
    if (wrappers.containsKey(listener)) {
      Events.remove(event, wrappers.remove(listener));
    }
  }

  private static final class EventListenerConsWrapper<E> implements Cons<E> {

    private final EventListener<E> listener;

    private EventListenerConsWrapper(final EventListener<E> listener) {
      this.listener = listener;
    }

    @Override
    public void get(final E e) {
      this.listener.onEvent(e);
    }
  }
}
