/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.plugin;

import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.event.EventHandler;
import fr.xpdustry.distributor.api.scheduler.Cancellable;
import fr.xpdustry.distributor.api.scheduler.TaskHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

final class SimplePluginAnnotationParser implements PluginAnnotationParser {

    private final MindustryPlugin plugin;

    SimplePluginAnnotationParser(final MindustryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void parse(final Object object) {
        for (final var method : object.getClass().getDeclaredMethods()) {
            parseEvents(object, method);
            parseTasks(object, method);
        }
    }

    private void parseEvents(final Object object, final Method method) {
        final var annotation = method.getAnnotation(EventHandler.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
        } else if (!method.canAccess(object)) {
            method.setAccessible(true);
        }

        final var handler = new MethodEventHandler<>(object, method, plugin);
        DistributorProvider.get()
                .getEventBus()
                .subscribe(handler.getEventType(), annotation.priority(), plugin, handler);
    }

    private void parseTasks(final Object object, final Method method) {
        final var annotation = method.getAnnotation(TaskHandler.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterCount() > 1) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
        } else if (!method.canAccess(object)) {
            method.setAccessible(true);
        } else if (method.getParameterCount() == 1 && !Cancellable.class.equals(method.getParameterTypes()[0])) {
            throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter type.");
        }

        final var scheduler = DistributorProvider.get().getPluginScheduler();
        final var builder = annotation.async() ? scheduler.scheduleAsync(plugin) : scheduler.scheduleSync(plugin);
        if (annotation.interval() > -1) {
            builder.repeat(annotation.interval(), annotation.unit());
        }
        if (annotation.delay() > -1) {
            builder.delay(annotation.delay(), annotation.unit());
        }
        builder.execute(new MethodTaskHandler(object, method));
    }

    private static final class MethodEventHandler<E> implements Consumer<E> {

        private final Object target;
        private final Method method;
        private final MindustryPlugin plugin;

        private MethodEventHandler(final Object target, final Method method, final MindustryPlugin plugin) {
            this.target = target;
            this.method = method;
            this.plugin = plugin;
        }

        @Override
        public void accept(final E event) {
            try {
                this.method.invoke(this.target, event);
            } catch (final InvocationTargetException e) {
                this.plugin
                        .getLogger()
                        .atError()
                        .setMessage("An error occurred while handling a {} event.")
                        .addArgument(event.getClass().getSimpleName())
                        .setCause(e.getTargetException())
                        .log();
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Failed to call " + this.method + " on " + this.target, e);
            }
        }

        @SuppressWarnings("unchecked")
        public Class<E> getEventType() {
            return (Class<E>) this.method.getParameterTypes()[0];
        }
    }

    private static final class MethodTaskHandler implements Consumer<Cancellable> {

        private final Object object;
        private final Method method;

        private MethodTaskHandler(final Object object, final Method method) {
            this.object = object;
            this.method = method;
        }

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
