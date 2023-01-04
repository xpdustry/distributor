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

import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.api.scheduler.PluginTask;
import fr.xpdustry.distributor.api.scheduler.PluginTaskRecipe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class RecipePluginTask<V> implements ScheduledPluginTask<V> {

    private final SimplePluginScheduler scheduler;
    private final ExtendedPlugin plugin;
    private final Iterable<RecipeStep<?, ?>> steps;
    private final CompletableFuture<V> completion = new CompletableFuture<>();
    private final Object initialObject;

    private RecipePluginTask(
            final SimplePluginScheduler scheduler, final ExtendedPlugin plugin, final Builder<V> recipe) {
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.steps = new ArrayList<>(recipe.steps);
        this.initialObject = recipe.initialObject;
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
        return this.plugin;
    }

    @Override
    public void run() {
        this.run0(this.initialObject, this.steps.iterator());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void run0(final Object current, final Iterator<RecipeStep<?, ?>> steps) {
        if (this.completion.isDone()) {
            return;
        }
        if (!steps.hasNext()) {
            this.completion.complete((V) current);
            return;
        }
        final RecipeStep step = steps.next();
        final var builder =
                step.async ? this.scheduler.scheduleAsync(this.plugin) : this.scheduler.scheduleSync(this.plugin);
        builder.execute(() -> {
            try {
                final var next = step.apply(current);
                this.run0(next, steps);
            } catch (final Throwable throwable) {
                this.completion.completeExceptionally(throwable);
                this.plugin.getLogger().error("An exception occurred in the scheduler with no handler", throwable);
            }
        });
    }

    @Override
    public long getNextExecutionTime() {
        return 0L;
    }

    private abstract static sealed class RecipeStep<T, R> implements Function<T, R> {

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

    public static final class Builder<V> implements PluginTaskRecipe<V> {

        private final SimplePluginScheduler scheduler;
        private final ExtendedPlugin plugin;
        private final Object initialObject;
        private final List<RecipeStep<?, ?>> steps;

        public Builder(
                final SimplePluginScheduler scheduler,
                final ExtendedPlugin plugin,
                final Object initialObject,
                final List<RecipeStep<?, ?>> steps) {
            this.scheduler = scheduler;
            this.plugin = plugin;
            this.initialObject = initialObject;
            this.steps = steps;
        }

        @Override
        public PluginTaskRecipe<V> thenAccept(final Consumer<V> consumer) {
            return this.withStep(new ConsumerRecipeStep<>(consumer, false));
        }

        @Override
        public <R> PluginTaskRecipe<R> thenApply(final Function<V, R> function) {
            return this.withStep(new FunctionRecipeStep<>(function, false));
        }

        @Override
        public PluginTaskRecipe<V> thenRun(final Runnable runnable) {
            return this.withStep(new RunnableRecipeStep<>(runnable, false));
        }

        @Override
        public PluginTaskRecipe<V> thenAcceptAsync(final Consumer<V> consumer) {
            return this.withStep(new ConsumerRecipeStep<>(consumer, true));
        }

        @Override
        public <R> PluginTaskRecipe<R> thenApplyAsync(final Function<V, R> function) {
            return this.withStep(new FunctionRecipeStep<>(function, true));
        }

        @Override
        public PluginTaskRecipe<V> thenRunAsync(final Runnable runnable) {
            return this.withStep(new RunnableRecipeStep<>(runnable, true));
        }

        @Override
        public PluginTask<V> execute() {
            final var task = new RecipePluginTask<>(this.scheduler, this.plugin, this);
            this.scheduler.schedule(task);
            return task;
        }

        private <I, O> PluginTaskRecipe<O> withStep(final RecipeStep<I, O> step) {
            final var steps = new ArrayList<>(this.steps);
            steps.add(step);
            return new Builder<>(this.scheduler, this.plugin, this.initialObject, steps);
        }
    }
}
