// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.FoundationAPI;
import com.xpdustry.foundation.event.EventSubscription;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryTask;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.stream.Stream;
import mindustry.Vars;

// TODO
//  Convert to a registry system with a PluginAnnotation meta annotation.
//  Would make filtering easier and allow to detect unused plugin annotations.

/// Centralized mechanism for processing annotations of plugin objects.
///
/// @param <R> the result type of the processor
@FunctionalInterface
public interface PluginAnnotationProcessor<R> {

    /// Processes [EventHandler] method annotations.
    ///
    /// The result is an [EventSubscription] tied to all created event listeners.
    ///
    /// If none are created, an empty result is returned.
    ///
    /// @param plugin the owning plugin facade
    /// @return a new event handler processor
    static PluginAnnotationProcessor<EventSubscription> events(final PluginFacade plugin) {
        return new EventHandlerProcessor(plugin, FoundationAPI.get().events());
    }

    /// Processes [ScheduledTaskHandler] method annotations.
    ///
    /// The result is a [MindustryTask] tied to all created tasks. If none are created, an empty result is returned.
    ///
    /// @param plugin the owning plugin facade
    /// @return a new task handler processor
    static PluginAnnotationProcessor<MindustryTask> scheduledTasks(final PluginFacade plugin) {
        return new ScheduledTaskHandlerProcessor(plugin, FoundationAPI.get().scheduler());
    }

    /// Processes [TriggerHandler] method annotations.
    ///
    /// The result is an [EventSubscription] tied to all created trigger listeners.
    /// If none are created, an empty result is returned.
    ///
    /// @param plugin the owning plugin facade
    /// @return a new trigger handler processor
    static PluginAnnotationProcessor<EventSubscription> triggers(final PluginFacade plugin) {
        return new TriggerHandlerProcessor(plugin, FoundationAPI.get().events());
    }

    /// Processes [PlayerActionHandler] method annotations.
    ///
    /// The result is an [EventSubscription] tied to all created filters. If none are created, an empty result is
    /// returned.
    ///
    /// @param plugin the owning plugin facade
    /// @return a new player action handler processor
    static PluginAnnotationProcessor<EventSubscription> playerActions(final PluginFacade plugin) {
        return new PlayerActionHandlerProcessor(plugin, Vars.netServer.admins);
    }

    /// Composes multiple processors into one that returns their results in a list.
    ///
    /// If a composed processor is in the list of processors, it is flattened. For example,
    /// `compose(p1, p2, compose(p3, p4), p5)` becomes `compose(p1, p2, p3, p4, p5)`.
    ///
    /// @param processors the processors
    /// @return the composed processor
    static PluginAnnotationProcessor<List<Object>> compose(final PluginAnnotationProcessor<?>... processors) {
        return compose(Arrays.asList(processors));
    }

    /// Composes multiple processors into one that returns their results in a list.
    ///
    /// @param processors the processors
    /// @return the composed processor
    static PluginAnnotationProcessor<List<Object>> compose(
            final SequencedCollection<PluginAnnotationProcessor<?>> processors) {
        return new CompositeAnnotationProcessor(processors.stream()
                .flatMap(processor -> processor instanceof CompositeAnnotationProcessor(var nested)
                        ? nested.stream()
                        : Stream.of(processor))
                .toList());
    }

    /// Processes the annotations of the given object.
    ///
    /// @param instance the object
    /// @return the result of the processing
    Optional<R> process(final Object instance);
}
