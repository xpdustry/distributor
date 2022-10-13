package fr.xpdustry.distributor.scheduler;

import cloud.commandframework.tasks.*;
import java.util.concurrent.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

final class SimplePluginTaskSynchronizer implements TaskSynchronizer {

  private final PluginScheduler scheduler;
  private final Plugin plugin;

  SimplePluginTaskSynchronizer(final PluginScheduler scheduler, final Plugin plugin) {
    this.scheduler = scheduler;
    this.plugin = plugin;
  }

  @Override
  public <I> CompletableFuture<Void> runSynchronous(@NotNull I input, @NotNull TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.syncTask(plugin, () -> {
      consumer.accept(input);
      future.complete(null);
    });
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runSynchronous(@NotNull I input, @NotNull TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.syncTask(plugin, () -> {
      future.complete(function.apply(input));
    });
    return future;
  }

  @Override
  public <I> CompletableFuture<Void> runAsynchronous(@NotNull I input, @NotNull TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.asyncTask(plugin, () -> {
      consumer.accept(input);
      future.complete(null);
    });
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runAsynchronous(@NotNull I input, @NotNull TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.syncTask(plugin, () -> {
      future.complete(function.apply(input));
    });
    return future;
  }
}
