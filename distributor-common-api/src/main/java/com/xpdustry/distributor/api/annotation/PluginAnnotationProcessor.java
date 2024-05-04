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

import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.Cancellable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A centralized mechanism for processing annotations of plugin objects.
 *
 * @param <R> the result type of the processor
 */
@FunctionalInterface
public interface PluginAnnotationProcessor<R> {

    /**
     * Processes the {@link EventHandler} method annotations of a given object to create event listeners.
     * The result is an {@link EventSubscription} tied to all created event listeners. If none, an empty result is returned.
     * <br>
     * This processor supports the {@link Async} annotation. Event handler methods will be executed asynchronously if present.
     *
     * @param plugin the owning plugin
     * @return a new event handler processor
     */
    static PluginAnnotationProcessor<EventSubscription> events(final MindustryPlugin plugin) {
        return new EventHandlerProcessor(plugin);
    }

    /**
     * Processes the {@link TaskHandler} method annotations of a given object to create tasks.
     * The result is a {@link Cancellable} tied to all created tasks. If none, an empty result is returned.
     * <br>
     * This processor supports the {@link Async} annotation. Task handler methods will be executed asynchronously if present.
     *
     * @param plugin the owning plugin
     * @return a new task handler processor
     */
    static PluginAnnotationProcessor<Cancellable> tasks(final MindustryPlugin plugin) {
        return new TaskHandlerProcessor(plugin);
    }

    /**
     * Composes multiple processors into one that returns their result in a list.
     * <br>
     * Note that if a composed processor is in the list of processors, it will be flattened. Such as:
     * <br>
     * {@code compose(p1, p2, compose(p3, p4), p5)} will become {@code compose(p1, p2, p3, p4, p5)}
     *
     * @param processors the processors
     * @return the composed processor
     */
    static PluginAnnotationProcessor<List<?>> compose(final PluginAnnotationProcessor<?>... processors) {
        return compose(Arrays.asList(processors));
    }

    /**
     * Composes multiple processors into one that returns their result in a list.
     * <br>
     * Note that if a composed processor is in the list of processors, it will be flattened. Such as:
     * <br>
     * {@code compose(p1, p2, compose(p3, p4), p5)} will become {@code compose(p1, p2, p3, p4, p5)}
     *
     * @param processors the processors
     * @return the composed processor
     */
    static PluginAnnotationProcessor<List<?>> compose(final Collection<PluginAnnotationProcessor<?>> processors) {
        return new CompositeAnnotationProcessor(processors.stream()
                .flatMap(processor -> processor instanceof CompositeAnnotationProcessor composite
                        ? composite.processors().stream()
                        : Stream.of(processor))
                .toList());
    }

    /**
     * Processes the annotations of the given object.
     *
     * @param instance the object
     * @return the result of the processing
     */
    Optional<R> process(final Object instance);
}
