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
package com.xpdustry.distributor.api.annotation.method;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.event.EventSubscription;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

final class EventHandlerProcessor implements MethodAnnotationScanner.Processor<EventHandler, EventSubscription> {

    @Override
    public Optional<EventSubscription> process(final MethodAnnotationScanner.Context<EventHandler> context) {
        if (context.getMethod().getParameterCount() != 1) {
            throw new IllegalArgumentException(
                    "The event handler on " + context.getMethod() + " hasn't the right parameter count.");
        } else if (!context.getMethod().canAccess(context.getInstance())) {
            context.getMethod().setAccessible(true);
        }
        final var handler = new MethodEventHandler<>(context.getInstance(), context.getMethod());
        return Optional.of(DistributorProvider.get()
                .getEventBus()
                .subscribe(handler.getEventType(), context.getAnnotation().priority(), context.getPlugin(), handler));
    }

    private record MethodEventHandler<E>(Object target, Method method) implements Consumer<E> {

        @Override
        public void accept(final E event) {
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
