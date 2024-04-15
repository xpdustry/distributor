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

import com.xpdustry.distributor.api.annotation.PluginAnnotationScanner;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginAware;
import com.xpdustry.distributor.api.scheduler.Cancellable;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.immutables.value.Value;

public interface MethodAnnotationScanner extends PluginAnnotationScanner<MethodAnnotationScanner.Result>, PluginAware {

    Key<TaskHandler, Cancellable> TASK_HANDLER_KEY = Key.of(TaskHandler.class, Cancellable.class);

    Key<EventHandler, EventSubscription> EVENT_HANDLER_KEY = Key.of(EventHandler.class, EventSubscription.class);

    KeyWithProcessor<TaskHandler, Cancellable> TASK_HANDLER_PAIR =
            KeyWithProcessor.of(TASK_HANDLER_KEY, new TaskHandlerProcessor());

    KeyWithProcessor<EventHandler, EventSubscription> EVENT_HANDLER_PAIR =
            KeyWithProcessor.of(EVENT_HANDLER_KEY, new EventHandlerProcessor());

    static MethodAnnotationScanner create(final MindustryPlugin plugin) {
        return new MethodAnnotationScannerImpl(plugin);
    }

    default <I extends Annotation, O> MethodAnnotationScanner register(
            final Key<I, O> key, final Processor<I, O> processor) {
        return register(KeyWithProcessor.of(key, processor));
    }

    <I extends Annotation, O> MethodAnnotationScanner register(final KeyWithProcessor<I, O> pair);

    @FunctionalInterface
    interface Processor<I extends Annotation, O> {

        Optional<O> process(final Context<I> context);
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Key<I extends Annotation, O> permits KeyImpl {

        static <I extends Annotation, O> Key<I, O> of(final Class<I> annotation, final Class<O> output) {
            return KeyImpl.of(annotation, output);
        }

        Class<I> getAnnotationClass();

        Class<O> getOutputClass();
    }

    @DistributorDataClass
    @Value.Immutable
    interface KeyWithProcessor<I extends Annotation, O> {

        static <I extends Annotation, O> KeyWithProcessor<I, O> of(
                final Key<I, O> key, final Processor<I, O> processor) {
            return KeyWithProcessorImpl.of(key, processor);
        }

        Key<I, O> getKey();

        Processor<I, O> getProcessor();
    }

    @DistributorDataClass
    @Value.Immutable
    interface Context<A extends Annotation> {

        static <A extends Annotation> Context<A> of(
                final Object instance, final Method method, final A annotation, final MindustryPlugin plugin) {
            return ContextImpl.of(instance, method, annotation, plugin);
        }

        Object getInstance();

        Method getMethod();

        A getAnnotation();

        MindustryPlugin getPlugin();
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Result permits ResultImpl {

        static Result.Builder builder() {
            return new ResultBuilderImpl();
        }

        Map<Key<?, ?>, List<?>> getOutputs();

        @SuppressWarnings("unchecked")
        default <I extends Annotation, O> List<O> getOutput(final Key<I, O> key) {
            return (List<O>) getOutputs().getOrDefault(key, List.of());
        }

        sealed interface Builder permits ResultBuilderImpl {

            <I extends Annotation, O> Builder addOutput(final Key<I, O> key, final O output);

            Result build();
        }
    }
}
