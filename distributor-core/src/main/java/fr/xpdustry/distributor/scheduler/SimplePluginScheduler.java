package fr.xpdustry.distributor.scheduler;

import arc.*;
import arc.util.*;
import cloud.commandframework.tasks.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

public final class SimplePluginScheduler implements PluginScheduler {

  private final ExecutorService executor;
  private final AtomicInteger idGenerator = new AtomicInteger();
  private final PriorityQueue<SimplePluginTask> tasks = new PriorityQueue<>();

  public SimplePluginScheduler(final int workers) {
    this.executor = Executors.newFixedThreadPool(workers, runnable -> {
      final var thread = new Thread(runnable);
      thread.setName("PluginSchedulerWorker - " + idGenerator.incrementAndGet());
      return thread;
    });
    Core.app.addListener(new ApplicationListener() {

      @Override
      public void update() {
        SimplePluginScheduler.this.update();
      }
    });
  }

  @Override
  public PluginTask syncTask(Plugin plugin, Runnable runnable) {
    return schedule(plugin, false, runnable, 0, -1);
  }

  @Override
  public PluginTask syncDelayedTask(Plugin plugin, Runnable runnable, int delay) {
    return schedule(plugin, false, runnable, delay, -1);
  }

  @Override
  public PluginTask syncRepeatingTask(Plugin plugin, Runnable runnable, int period) {
    return schedule(plugin, false, runnable, 0, period);
  }

  @Override
  public PluginTask syncRepeatingDelayedTask(Plugin plugin, Runnable runnable, int delay, int period) {
    return schedule(plugin, false, runnable, delay, period);
  }

  @Override
  public PluginTask asyncTask(Plugin plugin, Runnable runnable) {
    return schedule(plugin, true, runnable, 0, -1);
  }

  @Override
  public PluginTask asyncDelayedTask(Plugin plugin, Runnable runnable, int delay) {
    return schedule(plugin, true, runnable, delay, -1);
  }

  @Override
  public PluginTask asyncRepeatingTask(Plugin plugin, Runnable runnable, int period) {
    return schedule(plugin, true, runnable, 0, period);
  }

  @Override
  public PluginTask asyncRepeatingDelayedTask(Plugin plugin, Runnable runnable, int delay, int period) {
    return schedule(plugin, true, runnable, delay, period);
  }

  @Override
  public TaskSynchronizer getTaskSynchronizer(Plugin plugin) {
    return new SimplePluginTaskSynchronizer(this, plugin);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override
  public void shutdown() {
    this.executor.shutdown();
    try {
      this.executor.awaitTermination(15L, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      this.executor.shutdownNow();
    }
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
          task.future = CompletableFuture.runAsync(task.runnable, task.async ? this.executor : Core.app::post);
          task.latch.countDown();
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

  private SimplePluginTask schedule(final Plugin plugin, final boolean async, final Runnable runnable, final int delay, final int period) {
    final var future = new SimplePluginTask(plugin, async, period, runnable);
    future.nextRun = Time.globalTime + delay;
    tasks.add(future);
    return future;
  }

  private static final class SimplePluginTask implements PluginTask, Comparable<SimplePluginTask> {

    private final Plugin plugin;
    private final boolean async;
    private final int period;
    private final Runnable runnable;

    private float nextRun;
    @SuppressWarnings("NullAway")
    private @UnknownNullability CompletableFuture<Void> future = null;
    private boolean cancelled = false;
    private final CountDownLatch latch = new CountDownLatch(1);

    private SimplePluginTask(final Plugin plugin, boolean async, int period, Runnable runnable) {
      this.plugin = plugin;
      this.async = async;
      this.period = period;
      this.runnable = runnable;
    }

    @Override
    public boolean isAsync() {
      return async;
    }

    @Override
    public @NotNull Plugin getPlugin() {
      return plugin;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      this.cancelled = true;
      if (future != null) {
        return future.cancel(true);
      }
      return true;
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    @Override
    public boolean isDone() {
      return period != -1 && future != null && future.isDone();
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
      latch.await();
      return future.get();
    }

    @Override
    public Void get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      latch.await();
      return future.get(timeout, unit);
    }

    @Override
    public int compareTo(final SimplePluginTask o) {
      return Float.compare(this.nextRun, o.nextRun);
    }
  }
}
