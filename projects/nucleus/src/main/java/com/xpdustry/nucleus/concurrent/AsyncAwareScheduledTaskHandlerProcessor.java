// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.concurrent;

import com.xpdustry.foundation.FoundationAPI;
import com.xpdustry.foundation.annotation.ScheduledTaskHandler;
import com.xpdustry.foundation.annotation.ScheduledTaskHandlerProcessor;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryTask;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import com.xpdustry.nucleus.dependency.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AsyncAwareScheduledTaskHandlerProcessor extends ScheduledTaskHandlerProcessor
        implements PluginListener {

    private final List<MindustryAsyncTask> tasks = new ArrayList<>();

    @Inject
    public AsyncAwareScheduledTaskHandlerProcessor(final PluginFacade plugin) {
        super(plugin, FoundationAPI.get().scheduler());
    }

    @Override
    public void onExit() {
        this.tasks.forEach(MindustryAsyncTask::cancel);
    }

    @Override
    protected MindustryTask createTask(
            final Object instance, final Method method, final ScheduledTaskHandler annotation) {
        if (!method.isAnnotationPresent(Async.class)) {
            return super.createTask(instance, method, annotation);
        }
        final var handler = new MethodTaskHandler(instance, method);
        final var task = new MindustryAsyncTask(handler, annotation, this.plugin.logger());
        this.tasks.add(task);
        return task;
    }

    private static final class MindustryAsyncTask implements MindustryTask {

        private final ScheduledExecutorService scheduler;
        private final Future<?> future;

        private MindustryAsyncTask(
                final MethodTaskHandler handler, final ScheduledTaskHandler annotation, final PluginLogger logger) {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform()
                    .uncaughtExceptionHandler((thread, exception) -> logger.error(
                            "An uncaught error occurred in the async task thread {}", thread.getName(), exception))
                    .name("nucleus-scheduled-task-"
                            + handler.method().getDeclaringClass().getSimpleName() + "-"
                            + handler.method().getName())
                    .daemon()
                    .factory());

            final long initialDelay;
            final long delay;
            final TimeUnit unit;
            if (annotation.unit() == MindustryTimeUnit.TICKS) {
                initialDelay = MindustryTimeUnit.MILLIS.convert(annotation.initialDelay(), MindustryTimeUnit.TICKS);
                delay = MindustryTimeUnit.MILLIS.convert(annotation.delay(), MindustryTimeUnit.TICKS);
                unit = TimeUnit.MILLISECONDS;
            } else {
                initialDelay = annotation.initialDelay();
                delay = annotation.delay();
                unit = annotation.unit().asJavaTimeUnit().orElseThrow();
            }

            this.future = this.scheduler.scheduleWithFixedDelay(() -> handler.run(this), initialDelay, delay, unit);
        }

        @Override
        public State state() {
            return switch (this.future.state()) {
                case RUNNING -> State.SCHEDULED;
                case SUCCESS -> State.FINISHED;
                case FAILED, CANCELLED -> State.CANCELLED;
            };
        }

        @Override
        public void cancel() {
            this.future.cancel(true);
            this.scheduler.close();
        }
    }
}
