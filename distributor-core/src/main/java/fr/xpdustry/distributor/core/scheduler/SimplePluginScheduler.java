/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import fr.xpdustry.distributor.api.scheduler.PluginTaskBuilder;
import fr.xpdustry.distributor.api.scheduler.PluginTaskRecipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;

public final class SimplePluginScheduler implements PluginScheduler, PluginListener {

    static final String DISTRIBUTOR_WORKER_BASE_NAME = "distributor-worker-";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger("PluginScheduler");

    private final Queue<ScheduledPluginTask<?>> tasks =
            new PriorityBlockingQueue<>(16, Comparator.comparing(ScheduledPluginTask::getNextExecutionTime));
    private final ForkJoinPool pool;
    private final Executor syncExecutor;
    private final TimeSource source;

    public SimplePluginScheduler(final TimeSource source, final Executor syncExecutor, final int parallelism) {
        this.pool = new ForkJoinPool(parallelism, new PluginSchedulerWorkerThreadFactory(), null, false);
        this.syncExecutor = syncExecutor;
        this.source = source;
    }

    @Override
    public PluginTaskBuilder scheduleAsync(final MindustryPlugin plugin) {
        return new SimplePluginTask.Builder(this, plugin, true);
    }

    @Override
    public PluginTaskBuilder scheduleSync(final MindustryPlugin plugin) {
        return new SimplePluginTask.Builder(this, plugin, false);
    }

    @Override
    public <V> PluginTaskRecipe<V> recipe(final MindustryPlugin plugin, final V value) {
        return new RecipePluginTask.Builder<>(this, plugin, value, new ArrayList<>());
    }

    @Override
    public void onPluginUpdate() {
        while (!this.tasks.isEmpty()) {
            final var task = this.tasks.peek();
            if (task.isCancelled()) {
                this.tasks.remove();
            } else if (task.getNextExecutionTime() < this.source.getCurrentTicks()) {
                this.tasks.remove();
                final Executor executor = task.isAsync() ? this.pool : this.syncExecutor;
                executor.execute(task);
            } else {
                break;
            }
        }
    }

    @Override
    public void onPluginExit() {
        logger.info("Shutdown scheduler.");
        this.pool.shutdown();
        try {
            if (!this.pool.awaitTermination(20, TimeUnit.SECONDS)) {
                logger.error("Timed out waiting for the scheduler to terminate properly");
                Thread.getAllStackTraces().forEach((thread, stack) -> {
                    if (thread.getName().startsWith(DISTRIBUTOR_WORKER_BASE_NAME)) {
                        logger.error(
                                "Worker thread {} may be blocked, possibly the reason for the slow shutdown:\n{}",
                                thread.getName(),
                                Arrays.stream(stack).map(e -> "  " + e).collect(Collectors.joining("\n")));
                    }
                });
            }
        } catch (final InterruptedException e) {
            logger.error("The plugin scheduler shutdown have been interrupted.", e);
        }
    }

    void schedule(final ScheduledPluginTask<?> task) {
        this.tasks.add(task);
    }

    TimeSource getTimeSource() {
        return this.source;
    }

    boolean isShutdown() {
        return this.pool.isShutdown();
    }

    private static final class PluginSchedulerWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
            final var thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setName(DISTRIBUTOR_WORKER_BASE_NAME + COUNT.getAndIncrement());
            return thread;
        }
    }
}
