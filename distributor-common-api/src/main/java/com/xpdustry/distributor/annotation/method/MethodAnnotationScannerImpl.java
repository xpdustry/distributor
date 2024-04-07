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
package com.xpdustry.distributor.annotation.method;

import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MethodAnnotationScannerImpl implements MethodAnnotationScanner {

    private final MindustryPlugin plugin;
    private final Map<Class<? extends Annotation>, List<KeyWithProcessor<?, ?>>> processors = new HashMap<>();

    MethodAnnotationScannerImpl(final MindustryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <I extends Annotation, O> MethodAnnotationScanner register(final KeyWithProcessor<I, O> pair) {
        this.processors
                .computeIfAbsent(pair.getKey().getAnnotationClass(), k -> new ArrayList<>())
                .add(pair);
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Result scan(final Object instance) {
        final var builder = Result.builder();
        for (final var method : instance.getClass().getDeclaredMethods()) {
            for (final var annotation : method.getDeclaredAnnotations()) {
                final var pairs = processors.get(annotation.annotationType());
                if (pairs == null) continue;
                for (final KeyWithProcessor pair : pairs) {
                    final var output = pair.getProcessor().process(Context.of(instance, method, annotation, plugin));
                    if (output == null) continue;
                    builder.addOutput(pair.getKey(), output);
                }
            }
        }
        return builder.build();
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }
}
