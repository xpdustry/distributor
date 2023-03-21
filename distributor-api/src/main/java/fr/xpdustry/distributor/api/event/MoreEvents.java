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
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.plugin.PluginAware;
import fr.xpdustry.distributor.api.util.Priority;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for subscribing to events. A better alternative to {@link Events}.
 * <br>
 * Event subscribers registered with this class will not crash the server if an exception is thrown but be logged instead.
 * Subscribers also come with {@link EventSubscription} objects to dynamically unsubscribe them. And also
 * {@link Priority} to make sure your subscribers are called in a specific order.
 * <pre> {@code
 *      final MindustryPlugin plugin = ...;
 *      final EventSubscription subscription = MoreEvents.subscribe(EventType.PlayerJoin.class, plugin, event -> {
 *          event.player.sendMessage("Hello, " + event.player.name() + "!");
 *      });
 *      // When no longer needed, unsubscribe the listener
 *      subscription.unsubscribe();
 * } </pre>
 * <br>
 * This class also provides a way to subscribe to events using methods annotated with {@link EventHandler}.
 * <br>
 * <pre> {@code
 *      public final class PlayerListener {
 *          @EventHandler
 *          public void onPlayerJoin(final EventType.PlayerJoin event) {
 *              event.player.sendMessage("Hello, " + event.player.name() + "!");
 *          }
 *      }
 *
 *      public final class Plugin extends AbstractMindustryPlugin {
 *          @Override
 *          public void onInit() {
 *              MoreEvents.parse(this, new PlayerListener());
 *          }
 *      }
 * } </pre>
 */
@SuppressWarnings("unchecked")
public final class MoreEvents {

    static final ObjectMap<Object, Seq<Cons<?>>> events;

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof PriorityCons<?> m ? m.getPriority() : Priority.NORMAL;
        final var priorityB = b instanceof PriorityCons<?> m ? m.getPriority() : Priority.NORMAL;
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
     * @param event    the event class to subscribe to
     * @param priority the priority of the listener
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the event
     * @return the subscription of the subscribed listener
     */
    public static <E> EventSubscription subscribe(
            final Class<E> event, final Priority priority, final MindustryPlugin plugin, final Consumer<E> listener) {
        return MoreEvents.subscribe(event, new ConsumerCons<>(listener, priority, plugin));
    }

    /**
     * Subscribe to an event.
     *
     * @param event    the event class to subscribe to
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the event
     * @return the subscription of the subscribed listener
     */
    public static <E> EventSubscription subscribe(
            final Class<E> event, final MindustryPlugin plugin, final Consumer<E> listener) {
        return MoreEvents.subscribe(event, Priority.NORMAL, plugin, listener);
    }

    /**
     * Subscribe to an event.
     *
     * @param event    the event enum to subscribe to
     * @param priority the priority of the listener
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the enum event
     * @return the subscription of the subscribed listener
     */
    public static <E extends Enum<E>> EventSubscription subscribe(
            final E event, final Priority priority, final MindustryPlugin plugin, final Runnable listener) {
        return MoreEvents.subscribe(event, new ConsumerCons<>(e -> listener.run(), priority, plugin));
    }

    /**
     * Subscribe to an event.
     *
     * @param event    the event enum to subscribe to
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the enum event
     * @return the subscription of the subscribed listener
     */
    public static <E extends Enum<E>> EventSubscription subscribe(
            final E event, final MindustryPlugin plugin, final Runnable listener) {
        return MoreEvents.subscribe(event, Priority.NORMAL, plugin, listener);
    }

    private static <E> EventSubscription subscribe(final Object event, final PriorityCons<E> subscriber) {
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
     * Posts the event to the arc event bus.
     *
     * @param event the event to post
     * @param <E>   the type of the event
     */
    public static <E> void post(final E event) {
        Events.fire(event.getClass(), event);
    }

    /**
     * Posts the enum event to the arc event bus.
     *
     * @param event the enum event to post
     * @param <E>   the type of the enum event
     */
    public static <E extends Enum<E>> void post(final E event) {
        Events.fire(event);
    }

    /**
     * Parses the given listener to extract methods annotated with {@link EventHandler} and subscribes them to the
     * arc event bus.
     *
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to parse
     * @return the subscription of the subscribed handlers
     */
    public static EventSubscription parse(final MindustryPlugin plugin, final Object listener) {
        final List<EventSubscription> subscriptions = new ArrayList<>();
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

            final var cons = new MethodCons<>(listener, method, annotation.priority(), plugin);
            subscriptions.add(subscribe(cons.getEventType(), cons));
        }
        return () -> subscriptions.forEach(EventSubscription::unsubscribe);
    }

    private interface PriorityCons<T> extends Cons<T>, PluginAware {

        Priority getPriority();
    }

    private static final class ConsumerCons<T> implements PriorityCons<T> {

        private final Consumer<T> consumer;
        private final Priority priority;
        private final MindustryPlugin plugin;

        private ConsumerCons(final Consumer<T> consumer, final Priority priority, final MindustryPlugin plugin) {
            this.consumer = consumer;
            this.priority = priority;
            this.plugin = plugin;
        }

        @Override
        public void get(final T event) {
            try {
                this.consumer.accept(event);
            } catch (final Throwable e) {
                this.plugin
                        .getLogger()
                        .atError()
                        .setMessage("An error occurred while handling an {} event")
                        .addArgument(event.getClass().getSimpleName())
                        .setCause(e)
                        .log();
                this.plugin.getLogger().error("An error occurred while handling an event.", e);
            }
        }

        @Override
        public Priority getPriority() {
            return this.priority;
        }

        @Override
        public MindustryPlugin getPlugin() {
            return this.plugin;
        }
    }

    private static final class MethodCons<E> implements PriorityCons<E> {

        private final Object target;
        private final Method method;
        private final Priority priority;
        private final MindustryPlugin plugin;

        private MethodCons(
                final Object target, final Method method, final Priority priority, final MindustryPlugin plugin) {
            this.target = target;
            this.method = method;
            this.priority = priority;
            this.plugin = plugin;
        }

        @Override
        public void get(final E event) {
            try {
                this.method.invoke(this.target, event);
            } catch (final InvocationTargetException e) {
                this.plugin
                        .getLogger()
                        .atError()
                        .setMessage("An error occurred while handling an {} event: {}")
                        .addArgument(event.getClass().getSimpleName())
                        .setCause(e.getTargetException())
                        .log();
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Failed to call " + this.method + " on " + this.target, e);
            }
        }

        @SuppressWarnings("unchecked")
        public Class<E> getEventType() {
            return (Class<E>) this.method.getParameterTypes()[0];
        }

        @Override
        public Priority getPriority() {
            return this.priority;
        }

        @Override
        public MindustryPlugin getPlugin() {
            return this.plugin;
        }
    }
}
