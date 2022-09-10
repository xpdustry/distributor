package fr.xpdustry.distributor.scheduler;

import cloud.commandframework.tasks.*;
import fr.xpdustry.distributor.scheduler.old.*;
import java.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.*;

final class PluginTaskSynchronizer implements TaskSynchronizer {

  private final PluginScheduler scheduler;

  PluginTaskSynchronizer(final PluginScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public <I> CompletableFuture<Void> runSynchronous(@NonNull I input, @NonNull TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.schedule().withRunner(() -> {
      consumer.accept(input);
      future.complete(null);
    }).start();
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runSynchronous(@NonNull I input, @NonNull TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.schedule()
      .withRunner(() -> future.complete(function.apply(input)))
      .start();
    return future;
  }

  @Override
  public <I> CompletableFuture<Void> runAsynchronous(@NonNull I input, @NonNull TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    scheduler.schedule().withAsync().withRunner(() -> {
      consumer.accept(input);
      future.complete(null);
    }).start();
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runAsynchronous(@NonNull I input, @NonNull TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    scheduler.schedule().withAsync()
      .withRunner(() -> future.complete(function.apply(input)))
      .start();
    return future;
  }
}
