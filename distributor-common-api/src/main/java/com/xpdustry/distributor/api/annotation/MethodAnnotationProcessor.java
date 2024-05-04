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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

abstract class MethodAnnotationProcessor<A extends Annotation, R, O> implements PluginAnnotationProcessor<O> {

    private final Class<A> annotationType;

    protected MethodAnnotationProcessor(final Class<A> annotationType) {
        this.annotationType = annotationType;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public final Optional<O> process(final Object instance) {
        final List<R> results = new ArrayList<>();
        for (final var method : instance.getClass().getDeclaredMethods()) {
            for (final var annotation : method.getDeclaredAnnotations()) {
                if (this.annotationType != annotation.annotationType()) continue;
                results.add(this.process(instance, method, (A) annotation));
            }
        }
        return reduce(Collections.unmodifiableList(results));
    }

    protected abstract R process(final Object instance, final Method method, final A annotation);

    protected abstract Optional<O> reduce(final List<R> results);
}
