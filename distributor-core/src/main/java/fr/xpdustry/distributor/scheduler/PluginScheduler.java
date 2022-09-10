package fr.xpdustry.distributor.scheduler;

import cloud.commandframework.tasks.*;
import fr.xpdustry.distributor.plugin.*;

public interface PluginScheduler extends PluginAware {

  PluginTaskBuilder schedule();

  TaskSynchronizer asTaskSynchronizer();

  void shutdown(boolean now);
}
