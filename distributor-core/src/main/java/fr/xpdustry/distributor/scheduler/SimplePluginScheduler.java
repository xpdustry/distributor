package fr.xpdustry.distributor.scheduler;

import arc.*;
import arc.util.*;
import arc.util.Timer;
import cloud.commandframework.tasks.*;
import fr.xpdustry.distributor.scheduler.old.*;
import fr.xpdustry.distributor.util.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import mindustry.mod.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;

final class SimplePluginScheduler implements PluginScheduler {

  private final ExecutorService executor;
  private final Plugin plugin;
  private final AtomicInteger idGenerator = new AtomicInteger();
  private final PriorityQueue<SimplePluginTask> tasks = new PriorityQueue<>(Comparator.comparing(t -> t.nextRun));
  private @MonotonicNonNull TaskSynchronizer synchronizer;

  public SimplePluginScheduler(final Plugin plugin, final int workers) {
    this.plugin = plugin;
    this.executor = Executors.newFixedThreadPool(workers, runnable -> {
      final var thread = new Thread(runnable);
      thread.setName(Magik.getPluginNamespace(plugin) + " / MindustrySchedulerWorker - " + idGenerator.incrementAndGet());
      return thread;
    });
    Core.app.addListener(new ApplicationListener() {

      @Override
      public void update() {
        final var time = Time.globalTime;
        while (!SimplePluginScheduler.this.tasks.isEmpty()) {
          final var task = tasks.peek();
          if (task.isCancelled()) {
            SimplePluginScheduler.this.tasks.remove();
          } else if (task.nextRun < time) {
            SimplePluginScheduler.this.tasks.remove();
            if (task.future == null || task.future.isDone()) {
              task.future = CompletableFuture.runAsync(
                task.runner,
                task.async ? SimplePluginScheduler.this.executor : Core.app::post
              );
            }
            if (task.period != -1) {
              task.nextRun = time + task.period;
              SimplePluginScheduler.this.tasks.add(task);
            } else {
              task.future.complete(null);
            }
          } else {
            break;
          }
        }
      }
    });
  }

  @Override
  public PluginTaskBuilder schedule() {
    return new SimplePluginTaskBuilder();
  }

  @Override
  public void shutdown(boolean now) {
    if (now) {
      this.executor.shutdownNow();
    } else {
      this.executor.shutdown();
    }
  }

  @Override
  public synchronized TaskSynchronizer asTaskSynchronizer() {
    if (synchronizer == null) {
      synchronizer = new PluginTaskSynchronizer(this);
    }
    return synchronizer;
  }

  @Override
  public @NotNull Plugin getPlugin() {
    return plugin;
  }

  private void update() {
    final var time = Time.globalTime;
    while (!tasks.isEmpty()) {
      final var task = tasks.peek();
      if (task.isCancelled()) {
        tasks.remove();
      } else if (task.nextRun < time) {
        tasks.remove();
        if (task.future == null || task.future.isDone()) {
          task.future = CompletableFuture.runAsync(task.runner, task.async ? this.executor : Core.app::post);
        }
        if (task.period != -1) {
          task.nextRun = time + task.period;
          tasks.add(task);
        } else {
          task.future.complete(null);
        }
      } else {
        break;
      }
    }
  }

  private final class SimplePluginTaskBuilder implements PluginTaskBuilder {

    private boolean async = false;
    private int delay = 0;
    private int period = -1;
    private @MonotonicNonNull Runnable runner = null;

    @Override
    public PluginTaskBuilder withAsync(boolean async) {
      this.async = async;
      return this;
    }

    @Override
    public PluginTaskBuilder withDelay(int delay) {
      this.delay = delay;
      return this;
    }

    @Override
    public PluginTaskBuilder withRepeat(int period) {
      this.period = period;
      return this;
    }

    @Override
    public PluginTaskBuilder withRunner(Runnable runner) {
      this.runner = runner;
      return this;
    }

    @Override
    public PluginTask start() {
      final var task = new SimplePluginTask(async, period, runner);
      task.nextRun = Time.globalTime + delay;
      tasks.add(task);
      return task;
    }
  }

  private static final class SimplePluginTask implements PluginTask {

    private final boolean async;
    private final int period;
    private final Runnable runner;

    private float nextRun;
    private @MonotonicNonNull CompletableFuture<Void> future = null;
    private boolean cancelled = false;

    private SimplePluginTask(boolean async, int period, Runnable runner) {
      this.async = async;
      this.period = period;
      this.runner = runner;
    }

    @Override
    public boolean isAsync() {
      return async;
    }

    @Override
    public boolean isCompleted() {
      return period != -1 && future != null && future.isDone();
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    @Override
    public void cancel() {
      this.cancelled = true;
      if (future != null) {
        future.cancel(true);
      }
    }
  }
}
