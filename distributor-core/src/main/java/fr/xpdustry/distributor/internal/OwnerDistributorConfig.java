package fr.xpdustry.distributor.internal;

import fr.xpdustry.distributor.*;
import org.aeonbits.owner.*;

public interface OwnerDistributorConfig extends DistributorConfig, Accessible {

  @Config.Key("fr.xpdustry.distributor.scheduler.workers")
  @Config.DefaultValue("4")
  @Override
  int getSchedulerWorkers();
}
