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
package com.xpdustry.distributor.annotation;

import com.xpdustry.distributor.internal.DistributorDataClass;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import com.xpdustry.distributor.plugin.PluginAware;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.immutables.value.Value;

public interface PluginAnnotationScanner extends PluginAware {

    static PluginAnnotationScanner create(final MindustryPlugin plugin) {
        return new PluginAnnotationScannerImpl(plugin);
    }

    static ListenerDescriptor<EventHandler> createEventListener() {
        return ListenerDescriptor.of(new EventHandlerListener(), EventHandler.class);
    }

    static ListenerDescriptor<TaskHandler> createTaskListener() {
        return ListenerDescriptor.of(new TaskHandlerListener(), TaskHandler.class);
    }

    void scan(final Object object);

    default <A extends Annotation> PluginAnnotationScanner register(final ListenerDescriptor<A> descriptor) {
        return register(descriptor.getListenerClass(), descriptor.getListener());
    }

    <A extends Annotation> PluginAnnotationScanner register(final Class<A> clazz, final Listener<A> listener);

    @FunctionalInterface
    interface Listener<A extends Annotation> {

        void onMethodAnnotation(final Context<A> context);
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface ListenerDescriptor<A extends Annotation> permits ImmutableListenerDescriptor {

        static <A extends Annotation> ListenerDescriptor<A> of(final Listener<A> listener, final Class<A> clazz) {
            return ImmutableListenerDescriptor.of(listener, clazz);
        }

        Listener<A> getListener();

        Class<A> getListenerClass();
    }

    @DistributorDataClass
    @Value.Immutable
    interface Context<A extends Annotation> {

        static <A extends Annotation> PluginAnnotationScannerImpl.Context<A> of(
                final Object instance, final Method method, final A annotation, final MindustryPlugin plugin) {
            return ImmutableContext.of(instance, method, annotation, plugin);
        }

        Object getInstance();

        Method getMethod();

        A getAnnotation();

        MindustryPlugin getPlugin();
    }
}
