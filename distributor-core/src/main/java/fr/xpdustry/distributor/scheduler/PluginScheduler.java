package fr.xpdustry.distributor.scheduler;

import cloud.commandframework.tasks.*;
import mindustry.mod.*;

public interface PluginScheduler {

  PluginTask syncTask(final Plugin plugin, final Runnable runnable);

  PluginTask syncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay);

  PluginTask syncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period);

  PluginTask syncRepeatingDelayedTask(final Plugin plugin, final Runnable runnable, final int delay, final int period);

  PluginTask asyncTask(final Plugin plugin, final Runnable runnable);

  PluginTask asyncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay);

  PluginTask asyncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period);

  PluginTask asyncRepeatingDelayedTask(final Plugin plugin, final Runnable runnable, final int delay, final int period);

  TaskSynchronizer getTaskSynchronizer(final Plugin plugin);

  void shutdown();
}
