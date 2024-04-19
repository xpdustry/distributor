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
import com.xpdustry.distributor.api.scheduler.Cancellable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

final class TaskHandlerProcessor implements MethodAnnotationScanner.Processor<TaskHandler, Cancellable> {

    @Override
    public Optional<Cancellable> process(final MethodAnnotationScanner.Context<TaskHandler> context) {
        if (context.getMethod().getParameterCount() > 1) {
            throw new IllegalArgumentException(
                    "The event handler on " + context.getMethod() + " hasn't the right parameter count.");
        } else if (!context.getMethod().canAccess(context.getInstance())) {
            context.getMethod().setAccessible(true);
        } else if (context.getMethod().getParameterCount() == 1
                && !Cancellable.class.equals(context.getMethod().getParameterTypes()[0])) {
            throw new IllegalArgumentException(
                    "The event handler on " + context.getMethod() + " hasn't the right parameter type.");
        }

        final var builder = DistributorProvider.get()
                .getPluginScheduler()
                .schedule(context.getPlugin())
                .async(context.getAnnotation().async());
        if (context.getAnnotation().delay() > -1) {
            builder.delay(
                    context.getAnnotation().delay(), context.getAnnotation().unit());
        }
        if (context.getAnnotation().interval() > -1) {
            builder.repeat(
                    context.getAnnotation().interval(), context.getAnnotation().unit());
        }

        return Optional.of(builder.execute(new MethodTaskHandler(context.getInstance(), context.getMethod())));
    }

    private record MethodTaskHandler(Object object, Method method) implements Consumer<Cancellable> {

        @Override
        public void accept(final Cancellable cancellable) {
            try {
                if (this.method.getParameterCount() == 1) {
                    this.method.invoke(this.object, cancellable);
                } else {
                    this.method.invoke(this.object);
                }
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to invoke " + this.method, e);
            }
        }
    }
}
