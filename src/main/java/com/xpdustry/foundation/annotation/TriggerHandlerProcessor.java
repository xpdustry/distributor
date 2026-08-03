// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.event.EventSubscriber;
import com.xpdustry.foundation.event.EventSubscription;
import com.xpdustry.foundation.plugin.PluginFacade;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import mindustry.game.EventType;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.annotation.*")
public class TriggerHandlerProcessor
        extends MethodAnnotationProcessor<TriggerHandler, EventSubscription, EventSubscription> {

    protected final PluginFacade plugin;
    protected final EventPublisher events;

    protected TriggerHandlerProcessor(final PluginFacade plugin, final EventPublisher events) {
        super(TriggerHandler.class);
        this.plugin = plugin;
        this.events = events;
    }

    @Override
    protected EventSubscription process(final Object instance, final Method method, final TriggerHandler annotation) {
        if (method.getParameterCount() > 0) {
            throw new IllegalArgumentException("The trigger event handler on " + method + " has parameters.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        return this.events.subscribe(
                this.plugin,
                annotation.value(),
                annotation.priority(),
                new TriggerMethodEventHandler(instance, method));
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    public record TriggerMethodEventHandler(Object target, Method method)
            implements EventSubscriber<EventType.Trigger> {

        @Override
        public void onEvent(final EventType.Trigger event) {
            try {
                this.method.invoke(this.target);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Unable to invoke " + this.method + " on " + this.target, e);
            }
        }
    }
}
