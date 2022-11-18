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
package fr.xpdustry.distributor.api.scheduler;

import arc.ApplicationListener;
import arc.Core;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

final class PluginSchedulerImpl implements PluginScheduler {

    private final Queue<Task<?>> tasks = new PriorityBlockingQueue<>(16, Comparator.comparing(Task::getNextRun));
    private final ExtendedPlugin plugin;
    private final ForkJoinPool pool;
    private final Executor syncExecutor;
    private final PluginTimeSource source;

    PluginSchedulerImpl(
            final ExtendedPlugin plugin,
            final PluginTimeSource source,
            final Executor syncExecutor,
            final int parallelism) {
        this.plugin = plugin;
        this.syncExecutor = syncExecutor;
        this.pool = new ForkJoinPool(
                parallelism,
                new PluginSchedulerWorkerThreadFactory(),
                new PluginSchedulerUncaughtExceptionHandler(),
                false);
        this.source = source;
        Core.app.addListener(new ApplicationListener() {

            @Override
            public void update() {
                PluginSchedulerImpl.this.update();
            }
        });
    }

    PluginSchedulerImpl(final ExtendedPlugin plugin, final PluginTimeSource source, final Executor syncExecutor) {
        this(plugin, source, syncExecutor, Math.max(4, Runtime.getRuntime().availableProcessors()));
    }

    @Override
    public PluginTaskBuilder schedule() {
        return new SimplePluginTaskBuilder();
    }

    @Override
    public <V> PluginTaskRecipe<V> recipe(final V value) {
        return new SimplePluginTaskRecipe<>(value, new ArrayList<>());
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.pool;
    }

    @Override
    public Executor getSyncExecutor() {
        return this.syncExecutor;
    }

    @Override
    public PluginTimeSource getTimeSource() {
        return this.source;
    }

    @Override
    public void onPluginExit() {
        this.plugin.getLogger().info("Shutdown scheduler.");
        this.pool.shutdown();
        try {
            if (!this.pool.awaitTermination(20, TimeUnit.SECONDS)) {
                this.plugin.getLogger().error("Timed out waiting for the scheduler to terminate properly");
                Thread.getAllStackTraces().forEach((thread, stack) -> {
                    if (thread.getName().startsWith(this.getBaseWorkerName())) {
                        this.plugin
                                .getLogger()
                                .atWarn()
                                .setMessage("Thread {} may be blocked, possibly the reason for the slow shutdown:\n{}")
                                .addArgument(thread.getName())
                                .addArgument(
                                        Arrays.stream(stack).map(e -> "  " + e).collect(Collectors.joining("\n")))
                                .log();
                    }
                });
            }
        } catch (final InterruptedException e) {
            this.plugin.getLogger().error("The plugin scheduler shutdown have been interrupted.", e);
        }
    }

    @Override
    public ExtendedPlugin getPlugin() {
        return this.plugin;
    }

    void update() {
        while (!this.tasks.isEmpty()) {
            final var task = this.tasks.peek();
            if (task.isCancelled()) {
                this.tasks.remove();
            } else if (task.getNextRun() < this.source.getCurrentMillis()) {
                this.tasks.remove();
                final Executor executor = task.isAsync() ? this.pool : this.syncExecutor;
                executor.execute(task);
            } else {
                break;
            }
        }
    }

    String getBaseWorkerName() {
        return this.plugin.getDescriptor().getName() + "-worker-";
    }

    /* Scheduler specific internal classes */

    private interface Task<V> extends PluginTask<V>, Runnable {

        long getNextRun();
    }

    private abstract static class RecipeStep<T, R> implements Function<T, R> {

        public final boolean async;

        private RecipeStep(final boolean async) {
            this.async = async;
        }
    }

    private static final class ConsumerRecipeStep<T> extends RecipeStep<T, T> {

        private final Consumer<T> consumer;

        private ConsumerRecipeStep(final Consumer<T> consumer, final boolean async) {
            super(async);
            this.consumer = consumer;
        }

        @Override
        public T apply(final T object) {
            this.consumer.accept(object);
            return object;
        }
    }

    /* Basic plugin future */

    private static final class FunctionRecipeStep<T, R> extends RecipeStep<T, R> {

        private final Function<T, R> function;

        private FunctionRecipeStep(final Function<T, R> function, final boolean async) {
            super(async);
            this.function = function;
        }

        @Override
        public R apply(final T object) {
            return this.function.apply(object);
        }
    }

    private static final class RunnableRecipeStep<T> extends RecipeStep<T, T> {

        private final Runnable runnable;

        private RunnableRecipeStep(final Runnable runnable, final boolean async) {
            super(async);
            this.runnable = runnable;
        }

        @Override
        public T apply(final T object) {
            this.runnable.run();
            return object;
        }
    }

    /* Recipe black magik */

    private final class PluginSchedulerWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
            final var thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName(PluginSchedulerImpl.this.getBaseWorkerName() + COUNT.getAndIncrement());
            return thread;
        }
    }

    private final class PluginSchedulerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread thread, final Throwable throwable) {
            PluginSchedulerImpl.this
                    .plugin
                    .getLogger()
                    .atError()
                    .setMessage("An error occurred in thread {} of the plugin scheduler.")
                    .addArgument(Thread.currentThread().getName())
                    .setCause(throwable)
                    .log();
        }
    }

    private final class SimplePluginTask<V> extends FutureTask<V> implements Task<V> {

        private final boolean async;
        private final long period;
        private long nextRun;

        private SimplePluginTask(final Callable<V> callable, final boolean async, final long period) {
            super(callable);
            this.async = async;
            this.period = period;
        }

        @Override
        public void run() {
            if (PluginSchedulerImpl.this.pool.isShutdown()
                    && (this.period == 0 || this.nextRun - PluginSchedulerImpl.this.source.getCurrentMillis() > 0)) {
                this.cancel(false);
            } else if (this.period == 0) {
                super.run();
            } else if (super.runAndReset()) {
                this.schedule(this.period);
            }
        }

        @Override
        public boolean isAsync() {
            return this.async;
        }

        @Override
        public long getNextRun() {
            return this.nextRun;
        }

        @Override
        public ExtendedPlugin getPlugin() {
            return PluginSchedulerImpl.this.plugin;
        }

        private void schedule(final long period) {
            if (period >= 0) {
                this.nextRun = PluginSchedulerImpl.this.source.getCurrentMillis() + period;
                PluginSchedulerImpl.this.tasks.add(this);
            } else {
                this.nextRun += -period;
                PluginSchedulerImpl.this.tasks.add(this);
            }
        }
    }

    private final class SimplePluginTaskBuilder implements PluginTaskBuilder {

        private boolean async = false;
        private long initialDelay = 0;
        private long repeatPeriod = 0;

        @Override
        public PluginTaskBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public PluginTaskBuilder sync() {
            this.async = false;
            return this;
        }

        @Override
        public PluginTaskBuilder initialDelay(final long delay, final TimeUnit unit) {
            this.initialDelay = TimeUnit.MILLISECONDS.convert(delay, unit);
            return this;
        }

        @Override
        public PluginTaskBuilder repeatInterval(final long interval, final TimeUnit unit) {
            this.repeatPeriod = TimeUnit.MILLISECONDS.convert(interval, unit);
            return this;
        }

        @Override
        public PluginTaskBuilder repeatPeriod(final long period, final TimeUnit unit) {
            this.repeatPeriod = -TimeUnit.MILLISECONDS.convert(period, unit);
            return this;
        }

        @Override
        public PluginTask<Void> execute(final Runnable runnable) {
            return this.execute(Executors.callable(runnable, null));
        }

        @Override
        public <V> PluginTask<V> execute(final Callable<V> callable) {
            final var future = new SimplePluginTask<V>(callable, this.async, this.repeatPeriod);
            future.schedule(this.initialDelay);
            return future;
        }
    }

    private final class SimplePluginRecipeTask<V> implements Task<V> {

        private final Iterator<RecipeStep<?, ?>> steps;
        private final CompletableFuture<V> completion = new CompletableFuture<>();
        private Object current;

        private SimplePluginRecipeTask(final SimplePluginTaskRecipe<V> recipe) {
            this.steps = new ArrayList<>(recipe.steps).iterator();
            this.current = recipe.initialObject;
        }

        @Override
        public boolean isAsync() {
            return false;
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return this.completion.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.completion.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.completion.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return this.completion.get();
        }

        @Override
        public V get(final long timeout, final TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return this.completion.get(timeout, unit);
        }

        @Override
        public ExtendedPlugin getPlugin() {
            return PluginSchedulerImpl.this.plugin;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void run() {
            if (this.completion.isDone()) {
                return;
            }
            if (this.steps.hasNext()) {
                final RecipeStep step = this.steps.next();
                final var future = new SimplePluginTask<Void>(
                        () -> {
                            try {
                                SimplePluginRecipeTask.this.current = step.apply(this.current);
                                SimplePluginRecipeTask.this.run();
                            } catch (final Exception e) {
                                SimplePluginRecipeTask.this.completion.completeExceptionally(e);
                                throw e;
                            }
                            return null;
                        },
                        step.async,
                        0);
                future.schedule(0);
            } else {
                this.completion.complete((V) this.current);
            }
        }

        @Override
        public long getNextRun() {
            return 0L;
        }
    }

    private final class SimplePluginTaskRecipe<V> implements PluginTaskRecipe<V> {

        private final Object initialObject;
        private final List<RecipeStep<?, ?>> steps;

        private SimplePluginTaskRecipe(final Object initialObject, final List<RecipeStep<?, ?>> steps) {
            this.initialObject = initialObject;
            this.steps = steps;
        }

        @Override
        public PluginTaskRecipe<V> thenAccept(final Consumer<V> consumer) {
            this.steps.add(new ConsumerRecipeStep<>(consumer, false));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public <R> PluginTaskRecipe<R> thenApply(final Function<V, R> function) {
            this.steps.add(new FunctionRecipeStep<>(function, false));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public PluginTaskRecipe<V> thenRun(final Runnable runnable) {
            this.steps.add(new RunnableRecipeStep<>(runnable, false));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public PluginTaskRecipe<V> thenAcceptAsync(final Consumer<V> consumer) {
            this.steps.add(new ConsumerRecipeStep<>(consumer, true));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public <R> PluginTaskRecipe<R> thenApplyAsync(final Function<V, R> function) {
            this.steps.add(new FunctionRecipeStep<>(function, true));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public PluginTaskRecipe<V> thenRunAsync(final Runnable runnable) {
            this.steps.add(new RunnableRecipeStep<>(runnable, true));
            return new SimplePluginTaskRecipe<>(this.initialObject, this.steps);
        }

        @Override
        public PluginTask<V> execute() {
            final var future = new SimplePluginRecipeTask<>(this);
            PluginSchedulerImpl.this.tasks.add(future);
            return future;
        }
    }
}
