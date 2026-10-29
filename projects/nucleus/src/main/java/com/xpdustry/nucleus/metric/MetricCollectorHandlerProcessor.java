// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import com.xpdustry.foundation.annotation.PluginAnnotationProcessor;
import com.xpdustry.nucleus.concurrent.Async;
import com.xpdustry.nucleus.dependency.Inject;
import java.lang.reflect.Method;
import java.util.Optional;

public final class MetricCollectorHandlerProcessor implements PluginAnnotationProcessor<Void> {

    private final MetricRegistry registry;

    @Inject
    public MetricCollectorHandlerProcessor(final MetricRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<Void> process(final Object instance) {
        for (final var method : instance.getClass().getDeclaredMethods()) {
            final var annotation = method.getAnnotation(MetricCollectorHandler.class);
            if (annotation == null) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException(
                        "The metric collector handler on " + method + " has the wrong parameter count.");
            } else if (!MetricSink.class.equals(method.getParameterTypes()[0])) {
                throw new IllegalArgumentException(
                        "The metric collector handler on " + method + " has the wrong parameter type.");
            }
            if (!method.canAccess(instance)) {
                method.setAccessible(true);
            }
            final var async = method.isAnnotationPresent(Async.class);
            this.registry.register(new MethodMetricCollector(instance, method), async);
        }
        return Optional.empty();
    }

    private record MethodMetricCollector(Object target, Method method) implements MetricCollector {

        @Override
        public void flush(final MetricSink sink) {
            try {
                this.method.invoke(this.target, sink);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException("Unable to invoke " + this.method + " on " + this.target, e);
            }
        }
    }
}
