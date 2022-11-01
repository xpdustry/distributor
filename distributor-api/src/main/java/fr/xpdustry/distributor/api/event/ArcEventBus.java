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
package fr.xpdustry.distributor.api.event;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import fr.xpdustry.distributor.api.util.Priority;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ArcEventBus implements EventBus {

    static final ArcEventBus INSTANCE = new ArcEventBus();

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof MethodEventHandler<?> m ? m.priority : Priority.NORMAL;
        final var priorityB = b instanceof MethodEventHandler<?> m ? m.priority : Priority.NORMAL;
        return priorityA.compareTo(priorityB);
    };

    private final Map<Object, List<MethodEventHandler<?>>> listeners = new HashMap<>();
    private final ObjectMap<Class<?>, Seq<Cons<?>>> events;

    @SuppressWarnings("unchecked")
    ArcEventBus() {
        try {
            final var field = Events.class.getDeclaredField("events");
            field.setAccessible(true);
            this.events = (ObjectMap<Class<?>, Seq<Cons<?>>>) field.get(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void post(final Object event) {
        final var handlers = this.events.get(event.getClass());
        if (handlers != null) {
            for (final Cons subscriber : handlers.copy().sort(COMPARATOR)) {
                subscriber.get(event);
            }
        }
    }

    @Override
    public void register(final EventBusListener listener) {
        if (this.listeners.containsKey(listener)) {
            return;
        }

        final var handlers = new ArrayList<MethodEventHandler<?>>();
        for (final var method : listener.getClass().getDeclaredMethods()) {
            final var annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) {
                continue;
            } else if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException(
                        "The event handler on " + method + " hasn't the right parameter count.");
            } else if (!method.canAccess(listener) || !method.trySetAccessible()) {
                throw new RuntimeException("Unable to make " + method + " accessible.");
            }

            final var handler = new MethodEventHandler<>(listener, method, annotation.priority());
            this.events
                    .get(handler.getEventType(), () -> new Seq<>(Cons.class))
                    .add(handler)
                    .sort(COMPARATOR);
            handlers.add(handler);
        }

        this.listeners.put(listener, handlers);
    }

    @Override
    public void unregister(final EventBusListener listener) {
        final var handlers = this.listeners.remove(listener);
        if (handlers != null) {
            for (final var subscriber : handlers) {
                final var listeners = this.events.get(subscriber.getEventType());
                if (listeners != null && listeners.remove(subscriber) && listeners.size == 0) {
                    this.events.remove(subscriber.getEventType());
                }
            }
        }
    }

    private static final class MethodEventHandler<E> implements Cons<E> {

        private final Object target;
        private final Method method;
        private final Priority priority;

        private MethodEventHandler(final Object target, final Method method, final Priority priority) {
            this.target = target;
            this.method = method;
            this.priority = priority;
        }

        @Override
        public void get(final E event) {
            try {
                this.method.invoke(this.target, event);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Failed to call " + this.method + " on " + this.target, e);
            }
        }

        @SuppressWarnings("unchecked")
        public Class<E> getEventType() {
            return (Class<E>) this.method.getParameterTypes()[0];
        }
    }
}
