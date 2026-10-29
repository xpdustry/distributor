// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.pipeline;

import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.util.Priority;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jspecify.annotations.Nullable;

public abstract class AbstractProcessorPipeline<I, O> implements ProcessorPipeline<I, O> {

    private final String name;
    private final PluginLogger logger;
    private final List<ProcessorWithData<I, O>> processors = new CopyOnWriteArrayList<>();

    protected AbstractProcessorPipeline(final String name, final PluginLogger logger) {
        this.name = name;
        this.logger = logger;
    }

    protected final PluginLogger logger() {
        return this.logger;
    }

    protected final List<Processor<I, O>> processors() {
        return this.processors.stream().map(ProcessorWithData::processor).toList();
    }

    protected final @Nullable Processor<I, O> processor(final String name) {
        return this.processors.stream()
                .filter(processor -> processor.name().equals(name))
                .findFirst()
                .map(ProcessorWithData::processor)
                .orElse(null);
    }

    // TODO Unslop Later
    @Override
    public final synchronized void register(
            final String name, final Priority priority, final Processor<I, O> processor) {
        if (this.processors.stream().anyMatch(candidate -> candidate.name().equals(name))) {
            throw new IllegalArgumentException("Processor with name " + name + " already registered");
        }
        this.processors.add(new ProcessorWithData<>(processor, name, priority));
        this.processors.sort(Comparator.comparing(ProcessorWithData::priority));
        this.logger.debug("Registered processor {} with priority {} in the {} pipeline", name, priority, this.name);
    }

    private record ProcessorWithData<I, O>(Processor<I, O> processor, String name, Priority priority) {}
}
