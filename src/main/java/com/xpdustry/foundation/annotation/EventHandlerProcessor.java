// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.event.EventSubscriber;
import com.xpdustry.foundation.event.EventSubscription;
import com.xpdustry.foundation.plugin.PluginFacade;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.annotation.*")
public class EventHandlerProcessor
        extends MethodAnnotationProcessor<EventHandler, EventSubscription, EventSubscription> {

    protected final PluginFacade plugin;
    protected final EventPublisher events;

    protected EventHandlerProcessor(final PluginFacade plugin, final EventPublisher events) {
        super(EventHandler.class);
        this.plugin = plugin;
        this.events = events;
    }

    @Override
    protected EventSubscription process(final Object instance, final Method method, final EventHandler annotation) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("The event handler on " + method + " has the wrong parameter count.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        final var handler = new MethodEventHandler<>(instance, method);
        return this.events.subscribe(this.plugin, handler.eventType(), annotation.priority(), handler);
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    private record MethodEventHandler<E>(Object target, Method method) implements EventSubscriber<E> {

        @Override
        public void onEvent(final E event) {
            try {
                this.method.invoke(this.target, event);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Unable to invoke " + this.method + " on " + this.target, e);
            }
        }

        @SuppressWarnings("unchecked")
        private Class<E> eventType() {
            return (Class<E>) this.method.getParameterTypes()[0];
        }
    }
}
