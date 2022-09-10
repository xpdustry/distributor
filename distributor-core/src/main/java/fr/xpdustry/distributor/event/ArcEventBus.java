package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.function.*;

final class ArcEventBus extends AbstractEventBus {

  static final ArcEventBus INSTANCE = new ArcEventBus();

  private final ObjectMap<Class<?>, Seq<Cons<?>>> events;

  @SuppressWarnings("unchecked")
  private ArcEventBus() {
    try {
      final var field = Events.class.getDeclaredField("events");
      field.setAccessible(true);
      events = (ObjectMap<Class<?>, Seq<Cons<?>>>) field.get(null);
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected List<EventSubscriber<?>> getEventSubscribers(final Class<?> clazz) {
    return events.containsKey(clazz)
      ? Collections.emptyList()
      : new ArcList<>(events.get(clazz).map(c -> c instanceof EventSubscriber<?> subscriber
          ? subscriber
          : new EventSubscriberConsWrapper<>(c)
        )
      );
  }

  @Override
  public <E> EventSubscription subscribe(Class<E> event, EventSubscriber<E> subscriber) {
    final Cons<E> cons = subscriber::onEvent;
    if (subscriber.getPriority() == EventPriority.HIGHEST) {
      events.get(event, Seq::new).insert(0, cons);
    } else {
      events.get(event, Seq::new).add(cons);
    }
    return () -> {
      final var list = events.get(event);
      if (list != null) {
        list.remove(cons);
        if (list.isEmpty()) {
          events.remove(event);
        }
      }
    };
  }

  private static final class EventSubscriberConsWrapper<E> implements EventSubscriber<E> {

    private final Cons<E> subscriber;

    private EventSubscriberConsWrapper(final Cons<E> subscriber) {
      this.subscriber = subscriber;
    }

    @Override
    public void onEvent(final E event) {
      this.subscriber.get(event);
    }
  }
}
