// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

@API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.annotation.*")
public abstract class MethodAnnotationProcessor<A extends Annotation, R, O> implements PluginAnnotationProcessor<O> {

    protected final Class<A> annotationType;

    protected MethodAnnotationProcessor(final Class<A> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public Optional<O> process(final Object instance) {
        final List<R> results = new ArrayList<>();
        for (final var method : instance.getClass().getDeclaredMethods()) {
            for (final var annotation : method.getDeclaredAnnotations()) {
                if (!this.annotationType.equals(annotation.annotationType())) continue;
                final var result = this.process(instance, method, this.annotationType.cast(annotation));
                if (result != null) {
                    results.add(result);
                }
            }
        }
        return this.reduce(Collections.unmodifiableList(results));
    }

    protected abstract @Nullable R process(final Object instance, final Method method, final A annotation);

    protected abstract Optional<O> reduce(final List<R> results);
}
