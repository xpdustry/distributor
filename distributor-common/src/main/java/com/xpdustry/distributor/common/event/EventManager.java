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

import com.xpdustry.distributor.common.plugin.MindustryPlugin;
import com.xpdustry.distributor.common.util.Priority;
import java.util.function.Consumer;

/**
 * The event bus of this server. A better alternative to {@link arc.Events}.
 * <br>
 * Event subscribers registered with this class will not crash the server if an exception is thrown but be logged instead.
 * Subscribers also come with {@link EventSubscription} objects to dynamically unsubscribe them. And also
 * {@link Priority} to make sure your subscribers are called in a specific order.
 * <pre> {@code
 *      final EventBus bus = DistributorProvider.get().getEventBus();
 *      final MindustryPlugin plugin = ...;
 *      final EventSubscription subscription = bus.subscribe(EventType.PlayerJoin.class, plugin, event -> {
 *          event.player.sendMessage("Hello " + event.player.name() + "!");
 *      });
 *      // When no longer needed, you can unsubscribe the listener
 *      subscription.unsubscribe();
 * } </pre>
 * <br>
 */
public interface EventManager {

    static EventManager create() {
        return new EventManagerImpl();
    }

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
    <E> EventSubscription subscribe(
            final Class<E> event, final Priority priority, final MindustryPlugin plugin, final Consumer<E> listener);

    /**
     * Subscribe to an event.
     *
     * @param event    the event class to subscribe to
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the event
     * @return the subscription of the subscribed listener
     */
    default <E> EventSubscription subscribe(
            final Class<E> event, final MindustryPlugin plugin, final Consumer<E> listener) {
        return this.subscribe(event, Priority.NORMAL, plugin, listener);
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
    <E extends Enum<E>> EventSubscription subscribe(
            final E event, final Priority priority, final MindustryPlugin plugin, final Runnable listener);

    /**
     * Subscribe to an event.
     *
     * @param event    the event enum to subscribe to
     * @param plugin   the plugin that owns the listener
     * @param listener the listener to subscribe
     * @param <E>      the type of the enum event
     * @return the subscription of the subscribed listener
     */
    default <E extends Enum<E>> EventSubscription subscribe(
            final E event, final MindustryPlugin plugin, final Runnable listener) {
        return this.subscribe(event, Priority.NORMAL, plugin, listener);
    }

    /**
     * Posts the event to the arc event bus.
     *
     * @param event the event to post
     * @param <E>   the type of the event
     */
    <E> void post(final E event);

    /**
     * Posts the event to the arc event bus to the listeners of the given super class.
     *
     * @param clazz the class of the event
     * @param event the event to post
     * @param <E>   the type of the event
     */
    <E> void post(final Class<? super E> clazz, final E event);

    /**
     * Posts the enum event to the arc event bus.
     *
     * @param event the enum event to post
     * @param <E>   the type of the enum event
     */
    <E extends Enum<E>> void post(final E event);
}
