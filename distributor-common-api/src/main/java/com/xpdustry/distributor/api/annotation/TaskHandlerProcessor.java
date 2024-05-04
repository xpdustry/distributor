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
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.Cancellable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

final class TaskHandlerProcessor extends MethodAnnotationProcessor<TaskHandler, Cancellable, Cancellable> {

    private final MindustryPlugin plugin;

    TaskHandlerProcessor(final MindustryPlugin plugin) {
        super(TaskHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected Cancellable process(final Object instance, final Method method, final TaskHandler annotation) {
        if (method.getParameterCount() > 1) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
        } else if (!method.canAccess(instance)) {
            method.setAccessible(true);
        } else if (method.getParameterCount() == 1 && !Cancellable.class.equals(method.getParameterTypes()[0])) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter type.");
        }

        final var builder = DistributorProvider.get()
                .getPluginScheduler()
                .schedule(this.plugin)
                .async(method.isAnnotationPresent(Async.class));
        if (annotation.delay() > -1) {
            builder.delay(annotation.delay(), annotation.unit());
        }
        if (annotation.interval() > -1) {
            builder.repeat(annotation.interval(), annotation.unit());
        }

        return builder.execute(new MethodTaskHandler(instance, method));
    }

    @Override
    protected Optional<Cancellable> reduce(final List<Cancellable> results) {
        return results.isEmpty() ? Optional.empty() : Optional.of(() -> results.forEach(Cancellable::cancel));
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
