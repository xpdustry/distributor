package fr.xpdustry.distributor.scheduler;

import arc.*;
import arc.util.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import mindustry.mod.*;

public class SimplePluginScheduler implements PluginScheduler {

  private final Plugin plugin;
  private final ExecutorService service;

  private final PriorityQueue<PluginTask> tasks = new PriorityQueue<>();
  private final AtomicInteger idGenerator = new AtomicInteger();

  public SimplePluginScheduler(final Plugin plugin, final int workers) {
    this.plugin = plugin;
    this.service = Executors.newFixedThreadPool(workers, runnable -> {
      final var thread = new Thread(runnable);
      thread.setName(Magik.getPluginNamespace(plugin) + " / MindustrySchedulerWorker - " + idGenerator.incrementAndGet());
      return thread;
    });

    Core.app.addListener(new ApplicationListener() {

      @Override
      public void update() {
        final var time = Time.globalTime;
        while (!tasks.isEmpty()) {
          final var task = tasks.peek();
          if (task.nextRun < time) {
            tasks.remove();
            task.task.run();
          } else {
            break;
          }
        }
      }
    });
  }

  @Override
  public CompletableFuture<Void> scheduleRunnableTask(boolean async, float delay, Runnable runnable) {
    return CompletableFuture.runAsync(runnable, getDelayedExecutor(async, delay));
  }

  @Override
  public CompletableFuture<Void> scheduleRunnableTask(boolean async, Runnable runnable) {
    return CompletableFuture.runAsync(runnable, getDelayedExecutor(async, 0F));
  }

  @Override
  public <V> CompletableFuture<V> scheduleCompletableTask(boolean async, float delay, Supplier<V> supplier) {
    return CompletableFuture.supplyAsync(supplier, getDelayedExecutor(async, delay));
  }

  @Override
  public <V> CompletableFuture<V> scheduleCompletableTask(boolean async, Supplier<V> supplier) {
    return CompletableFuture.supplyAsync(supplier, getDelayedExecutor(async, 0F));
  }

  @Override
  public Future<Void> scheduleRepeatableTask(boolean async, float delay, float interval, Runnable runnable) {
    return new RepeatableFuture(async, delay, interval, runnable);
  }

  @Override
  public Future<Void> scheduleRepeatableTask(boolean async, float interval, Runnable runnable) {
    return new RepeatableFuture(async, 0F, interval, runnable);
  }

  @Override
  public void shutdown(boolean now) {
    if (now) {
      this.service.shutdownNow();
    } else {
      this.service.shutdown();
    }
  }

  @Override
  public Plugin getPlugin() {
    return this.plugin;
  }

  private DelayedExecutor getDelayedExecutor(final boolean async, final float delay) {
    return new DelayedExecutor(delay, async ? this.service : Core.app::post);
  }

  private static final class PluginTask implements Comparable<PluginTask> {

    private final float nextRun;
    private final Runnable task;

    private PluginTask(float nextRun, Runnable task) {
      this.nextRun = nextRun;
      this.task = task;
    }

    @Override
    public int compareTo(PluginTask o) {
      return Float.compare(this.nextRun, o.nextRun);
    }
  }

  private final class DelayedExecutor implements Executor {

    private final float delay;
    private final Executor executor;

    private DelayedExecutor(float delay, Executor executor) {
      this.delay = delay;
      this.executor = executor;
    }

    @Override
    public void execute(final Runnable command) {
      SimplePluginScheduler.this.tasks.add(
        new PluginTask(Time.globalTime + this.delay, () -> this.executor.execute(command))
      );
    }
  }

  private final class RepeatableFuture implements Future<Void> {

    private final CountDownLatch lock = new CountDownLatch(1);

    private final Executor executor;
    private final Runnable runnable;
    private CompletableFuture<Void> future;
    private boolean cancelled = false;

    private RepeatableFuture(boolean async, float delay, float interval, Runnable runnable) {
      this.executor = getDelayedExecutor(async, interval);
      this.runnable = runnable;
      this.future = SimplePluginScheduler.this.scheduleRunnableTask(async, delay, runnable);
      reschedule();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      cancelled = mayInterruptIfRunning;
      return future.cancel(true);
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public Void get() throws InterruptedException {
      lock.await();
      return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException {
      lock.await(timeout, unit);
      return null;
    }

    private void reschedule() {
      if (!cancelled) {
        if (future.isDone()) {
          future = CompletableFuture.runAsync(this.runnable, this.executor);
        }
        future.whenComplete((result, throwable) -> {
          if (throwable != null) {
            RepeatableFuture.this.cancelled = true;
            RepeatableFuture.this.lock.countDown();
          } else {
            RepeatableFuture.this.reschedule();
          }
        });
      }
    }
  }
}
