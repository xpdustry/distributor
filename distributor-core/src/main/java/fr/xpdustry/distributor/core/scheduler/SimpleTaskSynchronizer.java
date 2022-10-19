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

import cloud.commandframework.tasks.*;
import fr.xpdustry.distributor.api.scheduler.*;
import java.util.concurrent.*;
import mindustry.mod.*;

@SuppressWarnings("NullableProblems")
final class SimpleTaskSynchronizer implements TaskSynchronizer {

  private final PluginScheduler scheduler;
  private final Plugin plugin;

  SimpleTaskSynchronizer(final PluginScheduler scheduler, final Plugin plugin) {
    this.scheduler = scheduler;
    this.plugin = plugin;
  }

  @Override
  public <I> CompletableFuture<Void> runSynchronous(I input, TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.syncTask(plugin, () -> {
      consumer.accept(input);
      future.complete(null);
    });
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runSynchronous(I input, TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.syncTask(plugin, () -> {
      future.complete(function.apply(input));
    });
    return future;
  }

  @Override
  public <I> CompletableFuture<Void> runAsynchronous(I input, TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.asyncTask(plugin, () -> {
      consumer.accept(input);
      future.complete(null);
    });
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runAsynchronous(I input, TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.syncTask(plugin, () -> {
      future.complete(function.apply(input));
    });
    return future;
  }
}
