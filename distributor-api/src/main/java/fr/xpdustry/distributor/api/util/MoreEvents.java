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
package fr.xpdustry.distributor.api.util;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Utility class for events. A better alternative to {@link Events}.
 * <br>
 * Introducing the concept of subscription for making the use of dynamic listeners easier.
 * <pre> {@code
 *      final var subscription = MoreEvents.subscribe(EventType.PlayerJoin.class, event -> {
 *          event.player.sendMessage("Hello, " + event.player.name() + "!");
 *      });
 *      // When no longer needed, unsubscribe the listener
 *      subscription.unsubscribe();
 * } </pre>
 */
@SuppressWarnings("unchecked")
public final class MoreEvents {

    static final ObjectMap<Object, Seq<Cons<?>>> events;

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof ConsumerCons<?> m ? m.priority : Priority.NORMAL;
        final var priorityB = b instanceof ConsumerCons<?> m ? m.priority : Priority.NORMAL;
        return priorityA.compareTo(priorityB);
    };

    static {
        try {
            final var field = Events.class.getDeclaredField("events");
            field.setAccessible(true);
            events = (ObjectMap<Object, Seq<Cons<?>>>) field.get(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private MoreEvents() {}

    /**
     * Subscribe to an event.
     *
     * @param event the event class to subscribe to
     * @param priority the priority of the listener
     * @param listener the listener to subscribe
     * @return the subscription of the subscribed listener
     * @param <E> the type of the event
     */
    public static <E> Subscription subscribe(
            final Class<E> event, final Priority priority, final Consumer<E> listener) {
        return subscribe(event, new ConsumerCons<>(listener, priority));
    }

    /**
     * Subscribe to an event.
     *
     * @param event the event class to subscribe to
     * @param listener the listener to subscribe
     * @return the subscription of the subscribed listener
     * @param <E> the type of the event
     */
    public static <E> Subscription subscribe(final Class<E> event, final Consumer<E> listener) {
        return MoreEvents.subscribe(event, Priority.NORMAL, listener);
    }

    /**
     * Subscribe to an event.
     *
     * @param event the event enum to subscribe to
     * @param priority the priority of the listener
     * @param listener the listener to subscribe
     * @return the subscription of the subscribed listener
     * @param <E> the type of the enum event
     */
    public static <E extends Enum<E>> Subscription subscribe(
            final E event, final Priority priority, final Runnable listener) {
        return subscribe(event, new ConsumerCons<>(e -> listener.run(), priority));
    }

    /**
     * Subscribe to an event.
     *
     * @param event the event enum to subscribe to
     * @param listener the listener to subscribe
     * @return the subscription of the subscribed listener
     * @param <E> the type of the enum event
     */
    public static <E extends Enum<E>> Subscription subscribe(final E event, final Runnable listener) {
        return MoreEvents.subscribe(event, Priority.NORMAL, listener);
    }

    /**
     * Posts the event to the arc event bus.
     */
    public static <E> void post(final E event) {
        Events.fire(event.getClass(), event);
    }

    /**
     * Posts the enum event to the arc event bus.
     */
    public static <E extends Enum<E>> void post(final E event) {
        Events.fire(event);
    }

    private static <E> Subscription subscribe(final Object event, final ConsumerCons<E> subscriber) {
        MoreEvents.events
                .get(event, () -> new Seq<>(Cons.class))
                .add(subscriber)
                .sort(COMPARATOR);
        return () -> {
            final var subscribers = MoreEvents.events.get(event);
            if (subscribers != null) {
                subscribers.remove(subscriber);
                if (subscribers.isEmpty()) {
                    MoreEvents.events.remove(event);
                }
            }
        };
    }

    /**
     * A subscription to an event.
     */
    public interface Subscription {

        /**
         * Unsubscribes the bound subscriber from the event.
         */
        void unsubscribe();
    }

    private static final class ConsumerCons<T> implements Cons<T> {

        private final Consumer<T> consumer;
        private final Priority priority;

        private ConsumerCons(final Consumer<T> consumer, final Priority priority) {
            this.consumer = consumer;
            this.priority = priority;
        }

        @Override
        public void get(final T t) {
            this.consumer.accept(t);
        }
    }
}
