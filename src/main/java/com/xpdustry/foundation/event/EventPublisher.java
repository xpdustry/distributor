// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.event;

import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.util.Priority;

public interface EventPublisher {

    /// Subscribes to an event class with [Priority#NORMAL].
    ///
    /// @param facade the plugin facade that owns the subscription
    /// @param event the event class to subscribe to
    /// @param subscriber the subscriber to invoke
    /// @param <E> the type of the event
    /// @return the created subscription
    default <E> EventSubscription subscribe(
            final PluginFacade facade, final Class<E> event, final EventSubscriber<E> subscriber) {
        return this.subscribe(facade, event, Priority.NORMAL, subscriber);
    }

    /// Subscribes to an event class.
    ///
    /// @param facade the plugin facade that owns the subscription
    /// @param event the event class to subscribe to
    /// @param priority the priority of the subscriber
    /// @param subscriber the subscriber to invoke
    /// @param <E> the type of the event
    /// @return the created subscription
    <E> EventSubscription subscribe(
            final PluginFacade facade,
            final Class<E> event,
            final Priority priority,
            final EventSubscriber<E> subscriber);

    /// Subscribes to an enum event with [Priority#NORMAL].
    ///
    /// @param facade the plugin facade that owns the subscription
    /// @param event the enum event to subscribe to
    /// @param subscriber the subscriber to invoke
    /// @param <E> the type of the enum event
    /// @return the created subscription
    default <E extends Enum<E>> EventSubscription subscribe(
            final PluginFacade facade, final E event, final EventSubscriber<E> subscriber) {
        return this.subscribe(facade, event, Priority.NORMAL, subscriber);
    }

    /// Subscribes to an enum event.
    ///
    /// @param facade the plugin facade that owns the subscription
    /// @param event the enum event to subscribe to
    /// @param priority the priority of the subscriber
    /// @param subscriber the subscriber to invoke
    /// @param <E> the type of the enum event
    /// @return the created subscription
    <E extends Enum<E>> EventSubscription subscribe(
            final PluginFacade facade, final E event, final Priority priority, final EventSubscriber<E> subscriber);

    /// Publishes an event to the subscribers of the event bus.
    ///
    /// @param event the event to post
    /// @param <E> the type of the event
    @SuppressWarnings("unchecked")
    default <E> void publish(final E event) {
        this.publish((Class<E>) event.getClass(), event);
    }

    /// Publishes an event to subscribers of the given event type.
    ///
    /// @param type the event type to post to
    /// @param event the event to post
    /// @param <E> the type of the event
    <E> void publish(final Class<? super E> type, final E event);

    /// Publishes an enum event to the event bus.
    ///
    /// @param event the enum event to post
    /// @param <E> the type of the enum event
    <E extends Enum<E>> void publish(final E event);
}
