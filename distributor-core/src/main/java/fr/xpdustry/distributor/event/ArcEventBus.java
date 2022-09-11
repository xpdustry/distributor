package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import fr.xpdustry.distributor.event.EventSubscriber.*;
import java.lang.reflect.*;
import java.util.*;

final class ArcEventBus implements EventBus {

  static final ArcEventBus INSTANCE = new ArcEventBus();

  private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
    final var priorityA = a instanceof MethodEventSubscriber<?> m ? m.priority : Priority.NORMAL;
    final var priorityB = b instanceof MethodEventSubscriber<?> m ? m.priority : Priority.NORMAL;
    return priorityA.compareTo(priorityB);
  };

  private final Set<Object> objects = new HashSet<>();
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

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public EventPostResult post(final Object event) {
    Map<Object, List<Throwable>> exceptions = null;

    final var subscribers = events.get(event.getClass());
    if (subscribers != null) {
      for(final Cons subscriber : subscribers.copy().sort(COMPARATOR)) {
        try {
          subscriber.get(event);
        } catch (final Throwable e) {
          if (exceptions == null) {
            exceptions = new HashMap<>();
          }
          final var key = subscriber instanceof MethodEventSubscriber<?> m ? m.target : subscriber;
          exceptions.computeIfAbsent(key, k -> new ArrayList<>()).add(e);
        }
      }
    }

    return exceptions == null ? EventPostResult.success() : EventPostResult.failure(exceptions);
  }

  @Override
  public void register(final Object object) {
    if (!objects.add(object)) {
      return;
    }

    for (final var method : object.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(EventSubscriber.class);
      if (annotation == null) {
        continue;
      } else if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
      } else if (!method.canAccess(object) || !method.trySetAccessible()) {
        throw new RuntimeException("Unable to make " + method + " accessible.");
      }

      final var event = method.getParameterTypes()[0];
      final var subscriber = new MethodEventSubscriber<>(object, method, annotation.priority());
      if (subscriber.priority.ordinal() <= Priority.NORMAL.ordinal()) {
        events.get(event, () -> new Seq<>(Cons.class)).add(subscriber);
      } else {
        events.get(event, () -> new Seq<>(Cons.class)).insert(0, subscriber);
      }
    }
  }

  @Override
  public void unregister(final Object object) {
    if (!objects.remove(object)) {
      return;
    }

    final var entries = events.iterator();
    while (entries.hasNext()) {
      final var entry = entries.next();
      final var subscribers = entry.value.iterator();
      while (subscribers.hasNext()) {
        final var subscriber = subscribers.next();
        if (subscriber instanceof MethodEventSubscriber<?> m && m.target == object) {
          subscribers.remove();
        }
      }
      if (entry.value.isEmpty()) {
        entries.remove();
      }
    }
  }

  private static final class MethodEventSubscriber<T> implements Cons<T> {

    private final Object target;
    private final Method method;
    private final EventSubscriber.Priority priority;

    private MethodEventSubscriber(final Object target, final Method method, final Priority priority) {
      this.target = target;
      this.method = method;
      this.priority = priority;
    }

    @Override
    public void get(final T event) {
      try {
        this.method.invoke(event);
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException("Failed to call " + this.method + " on " + this.target, e);
      }
    }
  }
}
