package fr.xpdustry.distributor.util;

import arc.*;
import cloud.commandframework.tasks.*;
import java.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;

public final class ArcTaskSynchronizer implements TaskSynchronizer {

  private final Application application;

  public ArcTaskSynchronizer(final @NotNull Application application) {
    this.application = application;
  }

  @Override
  public <I> CompletableFuture<Void> runSynchronous(final @NonNull I input, final @NonNull TaskConsumer<I> consumer) {
    final var future = new CompletableFuture<Void>();
    application.post(() -> {
      consumer.accept(input);
      future.complete(null);
    });
    return future;
  }

  @Override
  public <I, O> CompletableFuture<O> runSynchronous(final @NonNull I input, final @NonNull TaskFunction<I, O> function) {
    final var future = new CompletableFuture<O>();
    application.post(() -> future.complete(function.apply(input)));
    return future;
  }

  @Override
  public <I> CompletableFuture<Void> runAsynchronous(final @NonNull I input, final @NonNull TaskConsumer<I> consumer) {
    return CompletableFuture.runAsync(() -> consumer.accept(input));
  }

  @Override
  public <I, O> CompletableFuture<O> runAsynchronous(final @NonNull I input, final @NonNull TaskFunction<I, O> function) {
    return CompletableFuture.supplyAsync(() -> function.apply(input));
  }
}
