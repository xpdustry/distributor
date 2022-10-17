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
package fr.xpdustry.distributor.scheduler;

import arc.*;
import arc.util.*;
import cloud.commandframework.tasks.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import mindustry.mod.*;
import org.jetbrains.annotations.ApiStatus.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

@Internal
public final class SimplePluginScheduler implements PluginScheduler, ApplicationListener {

  private static final Logger logger = LoggerFactory.getLogger(SimplePluginScheduler.class);

  private final ExecutorService executor;
  private final AtomicInteger idGenerator = new AtomicInteger();
  private final PriorityQueue<SimplePluginTask> tasks = new PriorityQueue<>();

  public SimplePluginScheduler(final int workers) {
    this.executor = Executors.newFixedThreadPool(workers, runnable -> {
      final var thread = new Thread(runnable);
      thread.setName("PluginSchedulerWorker - " + idGenerator.incrementAndGet());
      return thread;
    });
    Core.app.addListener(this);
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

  @Override
  public void update() {
    final var time = Time.globalTime;
    while (!tasks.isEmpty()) {
      final var task = tasks.peek();
      if (task.isCancelled()) {
        tasks.remove();
      } else if (task.nextRun < time) {
        tasks.remove();
        if (!task.isDone()) {
          final Executor executor = task.async ? this.executor : Core.app::post;
          if (task.period != 1) {
            executor.execute(task::runAndReset);
          } else {
            executor.execute(task);
          }
        }
        if (task.period != -1) {
          task.nextRun = time + task.period;
          tasks.add(task);
        }
      } else {
        break;
      }
    }
  }

  @Override
  public void dispose() {
    logger.info("Shutdown plugin scheduler.");
    executor.shutdown();
    try {
      if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
        executor.shutdownNow();
        if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
          logger.error("Failed to properly shutdown the plugin scheduler.");
        }
      }
    } catch (final InterruptedException ignored) {
      logger.warn("The plugin scheduler shutdown have been interrupted.");
    }
  }

  private SimplePluginTask schedule(final Plugin plugin, final boolean async, final Runnable runnable, final int delay, final int period) {
    final var future = new SimplePluginTask(runnable, plugin, async, period);
    future.nextRun = Time.globalTime + delay;
    tasks.add(future);
    logger.trace("A task has been scheduled by {} (async={}, delay={}, period={})", Magik.getDescriptor(plugin).getDisplayName(), async, delay, period);
    return future;
  }

  private static final class SimplePluginTask extends FutureTask<Void> implements PluginTask, Comparable<SimplePluginTask> {

    private final Plugin plugin;
    private final boolean async;
    private final int period;
    private float nextRun;

    private SimplePluginTask(final @NotNull Runnable runnable, final Plugin plugin, boolean async, int period) {
      super(runnable, null);
      this.plugin = plugin;
      this.async = async;
      this.period = period;
    }

    @Override
    public boolean runAndReset() {
      return super.runAndReset();
    }

    @Override
    protected void setException(Throwable t) {
      super.setException(t);
      logger.error("An error occurred in a scheduled task of " + Magik.getDescriptor(plugin).getDisplayName(), t);
    }

    @Override
    public @NotNull Plugin getPlugin() {
      return plugin;
    }

    @Override
    public boolean isAsync() {
      return async;
    }

    @Override
    public int compareTo(final @NotNull SimplePluginTask o) {
      return Float.compare(this.nextRun, o.nextRun);
    }
  }
}
