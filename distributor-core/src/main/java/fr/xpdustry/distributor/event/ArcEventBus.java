/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import java.lang.reflect.*;
import java.util.*;
import org.jetbrains.annotations.*;

final class ArcEventBus implements EventBus {

  static final ArcEventBus INSTANCE = new ArcEventBus();

  private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
    final var priorityA = a instanceof MethodEventHandler<?> m ? m.priority : EventPriority.NORMAL;
    final var priorityB = b instanceof MethodEventHandler<?> m ? m.priority : EventPriority.NORMAL;
    return priorityA.compareTo(priorityB);
  };

  private final Map<Object, List<MethodEventHandler<?>>> listeners = new HashMap<>();
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
  public void post(final @NotNull Object event) {
    final var handlers = events.get(event.getClass());
    if (handlers != null) {
      for (final Cons subscriber : handlers.copy().sort(COMPARATOR)) {
        subscriber.get(event);
      }
    }
  }

  @Override
  public void register(final @NotNull Object object) {
    if (listeners.containsKey(object)) {
      return;
    }

    final var handlers = new ArrayList<MethodEventHandler<?>>();
    for (final var method : object.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(EventHandler.class);
      if (annotation == null) {
        continue;
      } else if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
      } else if (!method.canAccess(object) || !method.trySetAccessible()) {
        throw new RuntimeException("Unable to make " + method + " accessible.");
      }

      final var handler = new MethodEventHandler<>(object, method, annotation.priority());
      events.get(handler.getEventType(), () -> new Seq<>(Cons.class)).add(handler).sort(COMPARATOR);
      handlers.add(handler);
    }

    listeners.put(object, handlers);
  }

  @Override
  public void unregister(final @NotNull Object object) {
    final var handlers = listeners.remove(object);
    if (handlers != null) {
      for (final var subscriber : handlers) {
        final var listeners = events.get(subscriber.getEventType());
        if (listeners != null && listeners.remove(subscriber) && listeners.size == 0) {
          events.remove(subscriber.getEventType());
        }
      }
    }
  }

  private static final class MethodEventHandler<E> implements Cons<E> {

    private final Object target;
    private final Method method;
    private final EventPriority priority;

    private MethodEventHandler(final @NotNull Object target, final @NotNull Method method, final @NotNull EventPriority priority) {
      this.target = target;
      this.method = method;
      this.priority = priority;
    }

    @Override
    public void get(final @NotNull E event) {
      try {
        this.method.invoke(event);
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException("Failed to call " + method + " on " + target, e);
      }
    }

    @SuppressWarnings("unchecked")
    public @NotNull Class<E> getEventType() {
      return (Class<E>) method.getParameterTypes()[0];
    }
  }
}
