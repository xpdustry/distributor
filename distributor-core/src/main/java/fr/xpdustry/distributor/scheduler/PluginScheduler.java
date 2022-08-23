package fr.xpdustry.distributor.scheduler;

import fr.xpdustry.distributor.plugin.*;
import java.util.concurrent.*;
import java.util.function.*;

// TODO Replace completable futures with dedicated Task classes
public interface PluginScheduler extends PluginAware {

  CompletableFuture<Void> scheduleRunnableTask(boolean async, float delay, Runnable runnable);

  CompletableFuture<Void> scheduleRunnableTask(boolean async, Runnable runnable);

  <V> CompletableFuture<V> scheduleCompletableTask(boolean async, float delay, Supplier<V> supplier);

  <V> CompletableFuture<V> scheduleCompletableTask(boolean async, Supplier<V> supplier);

  Future<Void> scheduleRepeatableTask(boolean async, float delay, float interval, Runnable runnable);

  Future<Void> scheduleRepeatableTask(boolean async, float interval, Runnable runnable);

  void shutdown(boolean now);
}
