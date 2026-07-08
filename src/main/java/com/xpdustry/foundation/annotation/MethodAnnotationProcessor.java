// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

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

    @SuppressWarnings("unchecked")
    @Override
    public final Optional<O> process(final Object instance) {
        final List<R> results = new ArrayList<>();
        for (final var method : instance.getClass().getDeclaredMethods()) {
            for (final var annotation : method.getDeclaredAnnotations()) {
                if (!this.annotationType.equals(annotation.annotationType())) continue;
                results.add(this.process(instance, method, (A) annotation));
            }
        }
        return this.reduce(Collections.unmodifiableList(results));
    }

    protected abstract R process(final Object instance, final Method method, final A annotation);

    protected abstract Optional<O> reduce(final List<R> results);
}
