package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import java.util.*;
import java.util.function.*;

final class MindustryEventBus extends AbstractEventBus {

  static final MindustryEventBus INSTANCE = new MindustryEventBus();

  @SuppressWarnings("rawtypes")
  private final Map<Consumer, EventListenerConsWrapper> wrappers = new HashMap<>();
  private final ObjectMap<Class<?>, Seq<Cons<?>>> events;

  @SuppressWarnings("unchecked")
  private MindustryEventBus() {
    try {
      final var field = Events.class.getDeclaredField("events");
      field.setAccessible(true);
      events = (ObjectMap<Class<?>, Seq<Cons<?>>>) field.get(null);
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <E> void post(final E event) {
    Events.fire(event);
  }

  @Override
  public <E> void register(Class<E> event, EventPriority priority, Consumer<E> listener) {
    final var wrapper = new EventListenerConsWrapper<>(listener);
    final var list = events.get(event, () -> new Seq<>(Cons.class));
    if (priority == EventPriority.FIRST) {
      list.add(wrapper);
    } else {
      list.insert(0, wrapper);
    }
    wrappers.put(listener, wrapper);
  }

  @Override
  public <E> void unregister(Class<E> event, Consumer<E> listener) {
    if (wrappers.containsKey(listener)) {
      final var list = events.get(event);
      if (list != null) {
        list.remove(wrappers.remove(listener));
        if (list.isEmpty()) {
          events.remove(event);
        }
      }
    }
  }

  private static final class EventListenerConsWrapper<E> implements Cons<E> {

    private final Consumer<E> listener;

    private EventListenerConsWrapper(final Consumer<E> listener) {
      this.listener = listener;
    }

    @Override
    public void get(final E e) {
      this.listener.accept(e);
    }
  }
}
