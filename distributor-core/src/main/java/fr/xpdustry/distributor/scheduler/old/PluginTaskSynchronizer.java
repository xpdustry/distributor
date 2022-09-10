package fr.xpdustry.distributor.scheduler.old;

import cloud.commandframework.tasks.*;
import java.util.concurrent.*;

@SuppressWarnings("NullableProblems")
public class PluginTaskSynchronizer implements TaskSynchronizer {

  private final PluginScheduler scheduler;

  public PluginTaskSynchronizer(final PluginScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public <I> CompletableFuture<Void> runSynchronous(final I input, final TaskConsumer<I> consumer) {
    return scheduler.scheduleRunnableTask(false, 0, () -> consumer.accept(input));
  }

  @Override
  public <I, O> CompletableFuture<O> runSynchronous(final I input, final TaskFunction<I, O> function) {
    return scheduler.scheduleCompletableTask(false, 0, () -> function.apply(input));
  }

  @Override
  public <I> CompletableFuture<Void> runAsynchronous(final I input, final TaskConsumer<I> consumer) {
    return scheduler.scheduleRunnableTask(true, 0, () -> consumer.accept(input));
  }

  @Override
  public <I, O> CompletableFuture<O> runAsynchronous(final I input, final TaskFunction<I, O> function) {
    return scheduler.scheduleCompletableTask(true, 0, () -> function.apply(input));
  }
}
