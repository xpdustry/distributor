/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.common.event;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginAware;
import com.xpdustry.distributor.api.util.Priority;
import java.util.Comparator;
import java.util.function.Consumer;

public final class EventBusImpl implements EventBus {

    private static final Comparator<Cons<?>> COMPARATOR = (a, b) -> {
        final var priorityA = a instanceof ConsumerCons<?> m ? m.priority : Priority.NORMAL;
        final var priorityB = b instanceof ConsumerCons<?> m ? m.priority : Priority.NORMAL;
        return priorityA.compareTo(priorityB);
    };

    final ObjectMap<Object, Seq<Cons<?>>> events;

    @SuppressWarnings("unchecked")
    public EventBusImpl() {
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

    private <E> EventSubscription subscribe(final Object event, final ConsumerCons<E> subscriber) {
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
    public <E> void post(final Class<? super E> clazz, final E event) {
        Events.fire(clazz, event);
    }

    @Override
    public <E extends Enum<E>> void post(final E event) {
        Events.fire(event);
    }

    private record ConsumerCons<T>(Consumer<T> consumer, Priority priority, MindustryPlugin plugin)
            implements Cons<T>, PluginAware {

        @Override
        public void get(final T event) {
            try {
                this.consumer.accept(event);
            } catch (final Throwable e) {
                this.plugin
                        .getLogger()
                        .atError()
                        .setMessage("An error occurred while handling a {} event.")
                        .addArgument(event.getClass().getSimpleName())
                        .setCause(e)
                        .log();
            }
        }

        @Override
        public MindustryPlugin getPlugin() {
            return this.plugin;
        }
    }
}
