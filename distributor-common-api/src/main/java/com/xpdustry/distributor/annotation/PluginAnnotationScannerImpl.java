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

import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class PluginAnnotationScannerImpl implements PluginAnnotationScanner {

    private final MindustryPlugin plugin;
    private final Map<Class<? extends Annotation>, Listener<?>> handlers = new HashMap<>();

    PluginAnnotationScannerImpl(final MindustryPlugin plugin) {
        this.plugin = plugin;
    }

    public <A extends Annotation> PluginAnnotationScanner register(final Class<A> clazz, final Listener<A> listener) {
        if (handlers.containsKey(clazz)) {
            throw new IllegalStateException(clazz + " is already registered.");
        }
        handlers.put(clazz, listener);
        return this;
    }

    @Override
    public void scan(final Object object) {
        for (final var method : object.getClass().getDeclaredMethods()) {
            for (final var annotation : method.getDeclaredAnnotations()) {
                scan(object, method, annotation);
            }
        }
    }

    private <A extends Annotation> void scan(final Object object, final Method method, final A annotation) {
        @SuppressWarnings("unchecked")
        final var handler = (Listener<A>) handlers.get(annotation.annotationType());
        if (handler == null) return;
        handler.onMethodAnnotation(Context.of(object, method, annotation, plugin));
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }
}
