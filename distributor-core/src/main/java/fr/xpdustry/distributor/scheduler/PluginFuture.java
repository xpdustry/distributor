package fr.xpdustry.distributor.scheduler;

import arc.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.util.*;
import java.util.concurrent.*;

public interface PluginFuture extends Future<Void>, PluginAware {

  boolean isPaused();

  interface Builder extends Buildable.Builder<PluginFuture> {

    Builder withAsync(boolean async);

    default Builder withAsync() {
      return withAsync(true);
    }

    Builder withPauseable(boolean pauseable);

    default Builder withPauseable() {
      Core.app.getListeners()
      return withPauseable(true);
    }

    Builder withDelay(int delay);

    Builder withRepeat(int period);

    Builder withRunner(Runnable runner);
  }
}
