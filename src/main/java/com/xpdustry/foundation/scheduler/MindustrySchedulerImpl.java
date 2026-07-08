// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class MindustrySchedulerImpl implements MindustryScheduler, PluginListener {

    private final MindustryTimeSource time;
    private boolean closed = false;
    private final Queue<MindustryTaskImpl> tasks =
            new PriorityBlockingQueue<>(4, Comparator.comparingLong(t -> t.nextExecutionTime));

    public MindustrySchedulerImpl(final MindustryTimeSource time) {
        this.time = time;
    }

    @Override
    public TaskBuilder newTaskBuilder(final PluginFacade plugin) {
        return new TaskBuilderImpl(plugin);
    }

    @Override
    public void onTick() {
        while (!this.tasks.isEmpty()) {
            final var task = this.tasks.peek();
            if (task.state() != MindustryTask.State.SCHEDULED) {
                this.tasks.remove();
            } else if (task.nextExecutionTime < this.time.ticks()) {
                this.tasks.remove();
                task.run();
            } else {
                break;
            }
        }
    }

    @Override
    public void onExit() {
        this.closed = true;
    }

    void schedule(final MindustryTaskImpl task, final long delay) {
        task.nextExecutionTime = MindustrySchedulerImpl.this.time.ticks() + delay;
        MindustrySchedulerImpl.this.tasks.add(task);
    }

    private final class TaskBuilderImpl implements MindustryScheduler.TaskBuilder {

        private final PluginFacade plugin;
        private long delay = -1;
        private long repeat = -1;

        private TaskBuilderImpl(final PluginFacade plugin) {
            this.plugin = plugin;
        }

        @Override
        public MindustryScheduler.TaskBuilder initialDelay(final long time, final MindustryTimeUnit unit) {
            this.delay = MindustryTimeUnit.TICKS.convert(time, unit);
            return this;
        }

        @Override
        public MindustryScheduler.TaskBuilder repeatWithDelay(final long time, final MindustryTimeUnit unit) {
            this.repeat = MindustryTimeUnit.TICKS.convert(time, unit);
            return this;
        }

        @Override
        public MindustryTask execute(final Consumer<MindustryTask> runnable) {
            final var task = new MindustryTaskImpl(this.plugin, this.repeat, runnable);
            if (MindustrySchedulerImpl.this.closed) {
                task.state.set(MindustryTask.State.CANCELLED);
            } else {
                MindustrySchedulerImpl.this.schedule(task, this.delay);
            }
            return task;
        }
    }

    private final class MindustryTaskImpl implements MindustryTask, Runnable {

        private final AtomicReference<State> state = new AtomicReference<>(State.SCHEDULED);

        private final PluginFacade plugin;
        private final long repeat;
        private final Consumer<MindustryTask> runnable;
        private long nextExecutionTime;

        private MindustryTaskImpl(
                final PluginFacade plugin, final long repeat, final Consumer<MindustryTask> runnable) {
            this.plugin = plugin;
            this.repeat = repeat;
            this.runnable = runnable;
        }

        @SuppressWarnings("NullAway") // bruh
        @Override
        public State state() {
            return this.state.get();
        }

        @Override
        public void cancel() {
            this.state.compareAndSet(State.SCHEDULED, State.CANCELLED);
        }

        @Override
        public void run() {
            if (this.state.get() != State.SCHEDULED) {
                return;
            }
            try {
                this.runnable.accept(this);
                if (this.repeat < 0) {
                    this.state.compareAndSet(State.SCHEDULED, State.FINISHED);
                } else {
                    MindustrySchedulerImpl.this.schedule(this, this.repeat);
                }
            } catch (final Throwable e) {
                this.state.compareAndSet(State.SCHEDULED, State.CANCELLED);
                this.plugin.logger().error("An uncaught exception occurred in a plugin scheduler task", e);
            }
        }
    }
}
