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

import arc.ApplicationListener;
import arc.Core;
import arc.util.Time;
import cloud.commandframework.tasks.TaskSynchronizer;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import fr.xpdustry.distributor.api.scheduler.PluginTask;
import fr.xpdustry.distributor.api.util.Magik;
import java.util.PriorityQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimplePluginScheduler implements PluginScheduler, ApplicationListener {

    private static final Logger logger = LoggerFactory.getLogger(SimplePluginScheduler.class);

    private final ExecutorService executor;
    private final AtomicInteger idGenerator = new AtomicInteger();
    private final PriorityQueue<SimplePluginTask> tasks = new PriorityQueue<>();

    public SimplePluginScheduler(final int workers) {
        this.executor = Executors.newFixedThreadPool(workers, runnable -> {
            final var thread = new Thread(runnable);
            thread.setName("PluginSchedulerWorker - " + this.idGenerator.incrementAndGet());
            return thread;
        });
        Core.app.addListener(this);
    }

    @Override
    public PluginTask syncTask(final Plugin plugin, final Runnable runnable) {
        return this.schedule(plugin, false, runnable, 0, -1);
    }

    @Override
    public PluginTask syncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay) {
        return this.schedule(plugin, false, runnable, delay, -1);
    }

    @Override
    public PluginTask syncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period) {
        return this.schedule(plugin, false, runnable, 0, period);
    }

    @Override
    public PluginTask syncRepeatingDelayedTask(
            final Plugin plugin, final Runnable runnable, final int delay, final int period) {
        return this.schedule(plugin, false, runnable, delay, period);
    }

    @Override
    public PluginTask asyncTask(final Plugin plugin, final Runnable runnable) {
        return this.schedule(plugin, true, runnable, 0, -1);
    }

    @Override
    public PluginTask asyncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay) {
        return this.schedule(plugin, true, runnable, delay, -1);
    }

    @Override
    public PluginTask asyncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period) {
        return this.schedule(plugin, true, runnable, 0, period);
    }

    @Override
    public PluginTask asyncRepeatingDelayedTask(
            final Plugin plugin, final Runnable runnable, final int delay, final int period) {
        return this.schedule(plugin, true, runnable, delay, period);
    }

    @Override
    public TaskSynchronizer getTaskSynchronizer(final Plugin plugin) {
        return new SimpleTaskSynchronizer(this, plugin);
    }

    @Override
    public void update() {
        final var time = Time.globalTime;
        while (!this.tasks.isEmpty()) {
            final var task = this.tasks.peek();
            if (task.isCancelled()) {
                this.tasks.remove();
            } else if (task.nextRun < time) {
                this.tasks.remove();
                if (!task.isDone()) {
                    final Executor executor = task.async ? this.executor : Core.app::post;
                    if (task.period != 1) {
                        executor.execute(task::runAndReset);
                    } else {
                        executor.execute(task);
                    }
                }
                if (task.period != -1) {
                    task.nextRun = time + task.period;
                    this.tasks.add(task);
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void dispose() {
        logger.info("Shutdown plugin scheduler.");
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(15, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(15, TimeUnit.SECONDS)) {
                    logger.error("Failed to properly shutdown the plugin scheduler.");
                }
            }
        } catch (final InterruptedException ignored) {
            logger.warn("The plugin scheduler shutdown have been interrupted.");
        }
    }

    private SimplePluginTask schedule(
            final Plugin plugin, final boolean async, final Runnable runnable, final int delay, final int period) {
        final var future = new SimplePluginTask(runnable, plugin, async, period);
        future.nextRun = Time.globalTime + delay;
        this.tasks.add(future);
        logger.trace(
                "A task has been scheduled by {} (async={}, delay={}, period={})",
                Magik.getDescriptor(plugin).getDisplayName(),
                async,
                delay,
                period);
        return future;
    }

    private static final class SimplePluginTask extends FutureTask<Void>
            implements PluginTask, Comparable<SimplePluginTask> {

        private final Plugin plugin;
        private final boolean async;
        private final int period;
        private float nextRun;

        private SimplePluginTask(final Runnable runnable, final Plugin plugin, final boolean async, final int period) {
            super(runnable, null);
            this.plugin = plugin;
            this.async = async;
            this.period = period;
        }

        @Override
        public boolean runAndReset() {
            return super.runAndReset();
        }

        @Override
        protected void setException(final Throwable t) {
            super.setException(t);
            logger.error(
                    "An error occurred in a scheduled task of "
                            + Magik.getDescriptor(this.plugin).getDisplayName(),
                    t);
        }

        @Override
        public Plugin getPlugin() {
            return this.plugin;
        }

        @Override
        public boolean isAsync() {
            return this.async;
        }

        @Override
        public int compareTo(final SimplePluginTask o) {
            return Float.compare(this.nextRun, o.nextRun);
        }
    }
}
