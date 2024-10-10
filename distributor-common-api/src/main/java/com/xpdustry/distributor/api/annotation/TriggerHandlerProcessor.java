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

final class TriggerHandlerProcessor
        extends MethodAnnotationProcessor<TriggerHandler, EventSubscription, EventSubscription> {

    private final MindustryPlugin plugin;

    TriggerHandlerProcessor(final MindustryPlugin plugin) {
        super(TriggerHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected EventSubscription process(final Object instance, final Method method, final TriggerHandler annotation) {
        if (method.getParameterCount() > 0) {
            throw new IllegalArgumentException("The trigger event handler on " + method + " has parameters.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        final var handler =
                new TriggerMethodEventHandler(instance, method, method.isAnnotationPresent(Async.class), this.plugin);
        return DistributorProvider.get()
                .getEventBus()
                .subscribe(annotation.value(), annotation.priority(), this.plugin, handler);
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    private record TriggerMethodEventHandler(Object target, Method method, boolean async, MindustryPlugin plugin)
            implements Runnable {

        @Override
        public void run() {
            if (this.async) {
                DistributorProvider.get()
                        .getPluginScheduler()
                        .schedule(this.plugin)
                        .async(true)
                        .execute(this::invoke);
            } else {
                this.invoke();
            }
        }

        private void invoke() {
            try {
                this.method.invoke(this.target);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Unable to invoke " + this.method + " on " + this.target, e);
            }
        }
    }
}
