/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.core.event;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import fr.xpdustry.distributor.api.event.EventBus;
import fr.xpdustry.distributor.api.event.EventHandler;
import fr.xpdustry.distributor.api.event.EventSubscription;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.plugin.PluginAware;
import fr.xpdustry.distributor.api.util.Priority;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public final class SimpleEventBus implements EventBus {

    final ObjectMap<Object, Seq<Cons<?>>> events;

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof PriorityCons<?> m ? m.getPriority() : Priority.NORMAL;
        final var priorityB = b instanceof PriorityCons<?> m ? m.getPriority() : Priority.NORMAL;
        return priorityA.compareTo(priorityB);
    };

    @SuppressWarnings("unchecked")
    public SimpleEventBus() {
        try {
            final var field = Events.class.getDeclaredField("events");
            field.setAccessible(true);
            this.events = (ObjectMap<Object, Seq<Cons<?>>>) field.get(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> EventSubscription subscribe(
            final Class<E> event, final Priority priority, final MindustryPlugin plugin, final Consumer<E> listener) {
        return this.subscribe(event, new ConsumerCons<>(listener, priority, plugin));
    }

    @Override
    public <E extends Enum<E>> EventSubscription subscribe(
            final E event, final Priority priority, final MindustryPlugin plugin, final Runnable listener) {
        return this.subscribe(event, new ConsumerCons<>(e -> listener.run(), priority, plugin));
    }

    private <E> EventSubscription subscribe(final Object event, final PriorityCons<E> subscriber) {
        this.events.get(event, () -> new Seq<>(Cons.class)).add(subscriber).sort(COMPARATOR);
        return () -> {
            final var subscribers = this.events.get(event);
            if (subscribers != null) {
                subscribers.remove(subscriber);
                if (subscribers.isEmpty()) {
                    this.events.remove(event);
                }
            }
        };
    }

    @Override
    public <E> void post(final E event) {
        Events.fire(event.getClass(), event);
    }

    @Override
    public <E extends Enum<E>> void post(final E event) {
        Events.fire(event);
    }

    @Override
    public EventSubscription parse(final MindustryPlugin plugin, final Object listener) {
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
            subscriptions.add(this.subscribe(cons.getEventType(), cons));
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
