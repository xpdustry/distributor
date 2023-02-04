/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core.scheduler;

import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.scheduler.Cancellable;
import fr.xpdustry.distributor.api.scheduler.PluginTask;
import fr.xpdustry.distributor.api.scheduler.PluginTaskBuilder;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public final class SimplePluginTask<V> extends FutureTask<V> implements ScheduledPluginTask<V> {

    private final MindustryPlugin plugin;
    private final boolean async;
    private final long period;
    private final SimplePluginScheduler scheduler;
    private long nextRun;

    private SimplePluginTask(
            final MindustryPlugin plugin,
            final Callable<V> callable,
            final boolean async,
            final long period,
            final SimplePluginScheduler scheduler) {
        super(callable);
        this.plugin = plugin;
        this.async = async;
        this.period = period;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        if (this.scheduler.isShutdown()
                && (this.period == 0
                        || this.nextRun - this.scheduler.getTimeSource().getCurrentTicks() > 0)) {
            this.cancel(false);
        } else if (this.period == 0) {
            super.run();
        } else if (super.runAndReset()) {
            this.nextRun = this.scheduler.getTimeSource().getCurrentTicks() + this.period;
            this.scheduler.schedule(this);
        }
    }

    @Override
    public boolean isAsync() {
        return this.async;
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    protected void setException(final Throwable t) {
        super.setException(t);
        this.plugin
                .getLogger()
                .error(
                        "An error occurred in thread {} of the plugin scheduler.",
                        Thread.currentThread().getName());
    }

    @Override
    public long getNextExecutionTime() {
        return this.nextRun;
    }

    public static final class Builder implements PluginTaskBuilder {

        private final SimplePluginScheduler scheduler;
        private final MindustryPlugin plugin;
        private final boolean async;
        private long delay = 0;
        private long repeat = 0;

        public Builder(final SimplePluginScheduler scheduler, final MindustryPlugin plugin, final boolean async) {
            this.scheduler = scheduler;
            this.plugin = plugin;
            this.async = async;
        }

        @Override
        public PluginTaskBuilder delay(final long delay) {
            this.delay = delay;
            return this;
        }

        @Override
        public PluginTaskBuilder repeat(final long interval) {
            this.repeat = interval;
            return this;
        }

        @Override
        public PluginTask<Void> execute(final Runnable runnable) {
            final var task = new SimplePluginTask<Void>(
                    this.plugin, Executors.callable(runnable, null), this.async, this.repeat, this.scheduler);
            return this.schedule(task);
        }

        @Override
        public PluginTask<Void> execute(final Consumer<Cancellable> consumer) {
            final var cancellable = new SimplePluginTaskCancellable();
            final var task = new SimplePluginTask<Void>(
                    this.plugin,
                    Executors.callable(() -> consumer.accept(cancellable), null),
                    this.async,
                    this.repeat,
                    this.scheduler);
            cancellable.task = task;
            return this.schedule(task);
        }

        @Override
        public <V> PluginTask<V> execute(final Supplier<V> supplier) {
            final var task =
                    new SimplePluginTask<>(this.plugin, supplier::get, this.async, this.repeat, this.scheduler);
            return this.schedule(task);
        }

        private <V> ScheduledPluginTask<V> schedule(final SimplePluginTask<V> task) {
            task.nextRun = this.scheduler.getTimeSource().getCurrentTicks() + this.delay;
            this.scheduler.schedule(task);
            return task;
        }
    }

    private static final class SimplePluginTaskCancellable implements Cancellable {

        private @MonotonicNonNull PluginTask<?> task = null;

        @Override
        public void cancel() {
            this.task.cancel(false);
        }
    }
}
