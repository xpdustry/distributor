package fr.xpdustry.distributor.scheduler;

import java.util.concurrent.*;

public interface PluginTask {

  boolean isAsync();

  boolean isCompleted();

  boolean isCancelled();

  void cancel();

  public static void main(String[] args) {
    ScheduledFuture
  }
}
