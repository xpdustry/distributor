package fr.xpdustry.distributor.scheduler;

import java.util.concurrent.*;

public interface PluginTaskBuilder {

  PluginTaskBuilder withAsync(boolean async);

  default PluginTaskBuilder withAsync() {
    return withAsync(true);
  }

  PluginTaskBuilder withPauseable(boolean pauseable);

  default PluginTaskBuilder withPauseable() {
    return withPauseable(true);
  }

  PluginTaskBuilder withDelay(int delay);

  PluginTaskBuilder withRepeat(int period);

  PluginTaskBuilder withRunner(Runnable runner);

  PluginTask start();

  public static void main(String[] args) {
    ScheduledFuture
  }
}
