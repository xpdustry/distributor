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
package com.xpdustry.distributor.scheduler;

import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.Cancellable;
import com.xpdustry.distributor.api.scheduler.MindustryTimeUnit;
import com.xpdustry.distributor.api.scheduler.PluginTask;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

final class PluginTaskImpl<V> extends FutureTask<V> implements PluginTask<V> {

    private final MindustryPlugin plugin;
    private final boolean async;
    private final long period;
    private final PluginSchedulerImpl scheduler;
    private long nextRun;

    private PluginTaskImpl(
            final MindustryPlugin plugin,
            final Callable<V> callable,
            final boolean async,
            final long period,
            final PluginSchedulerImpl scheduler) {
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
    protected void setException(final Throwable throwable) {
        super.setException(throwable);
        this.plugin
                .getLogger()
                .error(
                        "An error occurred in thread {} of the plugin scheduler.",
                        Thread.currentThread().getName(),
                        throwable);
    }

    public long getNextExecutionTime() {
        return this.nextRun;
    }

    static final class Builder implements PluginTask.Builder {

        private final PluginSchedulerImpl scheduler;
        private final MindustryPlugin plugin;
        private boolean async;
        private long delay = 0;
        private long repeat = 0;

        public Builder(final PluginSchedulerImpl scheduler, final MindustryPlugin plugin) {
            this.scheduler = scheduler;
            this.plugin = plugin;
        }

        @Override
        public PluginTask.Builder async(final boolean async) {
            this.async = async;
            return this;
        }

        @Override
        public PluginTask.Builder delay(final long delay, final MindustryTimeUnit unit) {
            this.delay = MindustryTimeUnit.TICKS.convert(delay, unit);
            return this;
        }

        @Override
        public PluginTask.Builder repeat(final long interval, final MindustryTimeUnit unit) {
            this.repeat = MindustryTimeUnit.TICKS.convert(interval, unit);
            return this;
        }

        @Override
        public PluginTask<Void> execute(final Runnable runnable) {
            final var task = new PluginTaskImpl<Void>(
                    this.plugin, Executors.callable(runnable, null), this.async, this.repeat, this.scheduler);
            return this.schedule(task);
        }

        @Override
        public PluginTask<Void> execute(final Consumer<Cancellable> consumer) {
            final var cancellable = new PluginTaskCancellable();
            final var task = new PluginTaskImpl<Void>(
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
            final var task = new PluginTaskImpl<>(this.plugin, supplier::get, this.async, this.repeat, this.scheduler);
            return this.schedule(task);
        }

        private <V> PluginTaskImpl<V> schedule(final PluginTaskImpl<V> task) {
            task.nextRun = this.scheduler.getTimeSource().getCurrentTicks() + this.delay;
            this.scheduler.schedule(task);
            return task;
        }
    }

    private static final class PluginTaskCancellable implements Cancellable {

        private @Nullable PluginTask<?> task = null;

        @Override
        public void cancel() {
            Objects.requireNonNull(this.task).cancel();
        }
    }
}
