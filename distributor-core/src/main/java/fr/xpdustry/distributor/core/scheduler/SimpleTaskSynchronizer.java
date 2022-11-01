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

import cloud.commandframework.tasks.TaskConsumer;
import cloud.commandframework.tasks.TaskFunction;
import cloud.commandframework.tasks.TaskSynchronizer;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import java.util.concurrent.CompletableFuture;
import mindustry.mod.Plugin;

@SuppressWarnings("NullableProblems")
final class SimpleTaskSynchronizer implements TaskSynchronizer {

    private final PluginScheduler scheduler;
    private final Plugin plugin;

    SimpleTaskSynchronizer(final PluginScheduler scheduler, final Plugin plugin) {
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public <I> CompletableFuture<Void> runSynchronous(final I input, final TaskConsumer<I> consumer) {
        final var future = new CompletableFuture<Void>();
        this.scheduler.syncTask(this.plugin, () -> {
            consumer.accept(input);
            future.complete(null);
        });
        return future;
    }

    @Override
    public <I, O> CompletableFuture<O> runSynchronous(final I input, final TaskFunction<I, O> function) {
        final var future = new CompletableFuture<O>();
        this.scheduler.syncTask(this.plugin, () -> {
            future.complete(function.apply(input));
        });
        return future;
    }

    @Override
    public <I> CompletableFuture<Void> runAsynchronous(final I input, final TaskConsumer<I> consumer) {
        final var future = new CompletableFuture<Void>();
        this.scheduler.asyncTask(this.plugin, () -> {
            consumer.accept(input);
            future.complete(null);
        });
        return future;
    }

    @Override
    public <I, O> CompletableFuture<O> runAsynchronous(final I input, final TaskFunction<I, O> function) {
        final var future = new CompletableFuture<O>();
        this.scheduler.syncTask(this.plugin, () -> {
            future.complete(function.apply(input));
        });
        return future;
    }
}
