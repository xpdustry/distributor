// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.event;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.util.Priority;
import java.util.Comparator;

/// Default [EventPublisher] implementation backed by `arc.Events`.
public final class EventPublisherImpl implements EventPublisher {

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof EventListenerAsCons<?> m ? m.priority : Priority.NORMAL;
        final var priorityB = b instanceof EventListenerAsCons<?> m ? m.priority : Priority.NORMAL;
        return priorityA.compareTo(priorityB);
    };

    static final ObjectMap<Object, Seq<Cons<?>>> EVENTS_MAP = getEventsMap();

    @SuppressWarnings("unchecked")
    private static ObjectMap<Object, Seq<Cons<?>>> getEventsMap() {
        try {
            final var field = Events.class.getDeclaredField("events");
            field.setAccessible(true);
            return (ObjectMap<Object, Seq<Cons<?>>>) field.get(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> EventSubscription subscribe(
            final PluginFacade plugin,
            final Class<E> event,
            final Priority priority,
            final EventSubscriber<E> subscriber) {
        return this.subscribe0(plugin, event, priority, subscriber);
    }

    @Override
    public <E extends Enum<E>> EventSubscription subscribe(
            final PluginFacade plugin, final E event, final Priority priority, final EventSubscriber<E> subscriber) {
        return this.subscribe0(plugin, event, priority, subscriber);
    }

    @SuppressWarnings("ConstantValue") // for "subscribers != null"
    private <E> EventSubscription subscribe0(
            final PluginFacade plugin,
            final Object event,
            final Priority priority,
            final EventSubscriber<E> subscriber) {
        final var cons = new EventListenerAsCons<>(subscriber, priority, plugin);
        EVENTS_MAP.get(event, () -> new Seq<>(Cons.class)).add(cons).sort(COMPARATOR);
        return () -> {
            final var subscribers = EVENTS_MAP.get(event);
            if (subscribers != null) {
                subscribers.remove(cons);
                if (subscribers.isEmpty()) {
                    EVENTS_MAP.remove(event);
                }
            }
        };
    }

    @Override
    public <E> void publish(final Class<? super E> type, E event) {
        Events.fire(type, event);
    }

    @Override
    public <E extends Enum<E>> void publish(final E event) {
        Events.fire(event);
    }

    private record EventListenerAsCons<E>(EventSubscriber<E> subscriber, Priority priority, PluginFacade plugin)
            implements Cons<E> {
        @Override
        public void get(final E event) {
            try {
                this.subscriber.onEvent(event);
            } catch (final Throwable e) {
                this.plugin
                        .logger()
                        .error(
                                "An error occurred while handling a {} event.",
                                event.getClass().getSimpleName(),
                                e);
            }
        }
    }
}
