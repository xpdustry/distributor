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
package com.xpdustry.distributor.api.annotation;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

final class EventHandlerProcessor
        extends MethodAnnotationProcessor<EventHandler, EventSubscription, EventSubscription> {

    private final MindustryPlugin plugin;

    EventHandlerProcessor(final MindustryPlugin plugin) {
        super(EventHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected EventSubscription process(final Object instance, final Method method, final EventHandler annotation) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        final var handler =
                new MethodEventHandler<>(instance, method, method.isAnnotationPresent(Async.class), this.plugin);
        return DistributorProvider.get()
                .getEventBus()
                .subscribe(handler.getEventType(), annotation.priority(), this.plugin, handler);
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    private record MethodEventHandler<E>(Object target, Method method, boolean async, MindustryPlugin plugin)
            implements Consumer<E> {

        @Override
        public void accept(final E event) {
            if (this.async) {
                DistributorProvider.get()
                        .getPluginScheduler()
                        .schedule(this.plugin)
                        .async(true)
                        .execute(() -> this.invoke(event));
            } else {
                this.invoke(event);
            }
        }

        private void invoke(final E event) {
            try {
                this.method.invoke(this.target, event);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Unable to invoke " + this.method + " on " + this.target, e);
            }
        }

        @SuppressWarnings("unchecked")
        private Class<E> getEventType() {
            return (Class<E>) this.method.getParameterTypes()[0];
        }
    }
}
