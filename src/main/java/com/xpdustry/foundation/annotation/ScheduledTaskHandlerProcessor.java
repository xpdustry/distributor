// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.FoundationAPI;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryTask;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.annotation.*")
public class ScheduledTaskHandlerProcessor
        extends MethodAnnotationProcessor<ScheduledTaskHandler, MindustryTask, MindustryTask> {

    protected final PluginFacade plugin;

    protected ScheduledTaskHandlerProcessor(final PluginFacade plugin) {
        super(ScheduledTaskHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected MindustryTask process(final Object instance, final Method method, final ScheduledTaskHandler annotation) {
        if (method.getParameterCount() > 0) {
            throw new IllegalArgumentException(
                    "The scheduled task handler on " + method + " should not have any parameters.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        return this.createTask(instance, method, annotation);
    }

    protected MindustryTask createTask(
            final Object instance, final Method method, final ScheduledTaskHandler annotation) {
        return FoundationAPI.get()
                .scheduler()
                .newTaskBuilder(this.plugin)
                .initialDelay(annotation.initialDelay(), annotation.unit())
                .repeatWithDelay(annotation.delay(), annotation.unit())
                .execute(new MethodTaskHandler(instance, method));
    }

    @Override
    protected Optional<MindustryTask> reduce(final List<MindustryTask> results) {
        if (results.isEmpty()) {
            return Optional.empty();
        } else if (results.size() == 1) {
            return Optional.of(results.getFirst());
        } else {
            return Optional.of(new CompositeTask(results));
        }
    }

    public record MethodTaskHandler(Object object, Method method) implements Runnable {

        @Override
        public void run() {
            try {
                this.method.invoke(this.object);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to invoke " + this.method, e);
            }
        }
    }

    public record CompositeTask(List<MindustryTask> tasks) implements MindustryTask {

        @Override
        public State state() {
            if (this.tasks.stream().anyMatch(task -> task.state() == State.SCHEDULED)) {
                return State.SCHEDULED;
            }
            return this.tasks.stream().allMatch(task -> task.state() == State.CANCELLED)
                    ? State.CANCELLED
                    : State.FINISHED;
        }

        @Override
        public void cancel() {
            this.tasks.forEach(MindustryTask::cancel);
        }
    }
}
