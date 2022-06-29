package fr.xpdustry.distributor.legacy.internal;

import org.aeonbits.owner.*;

public interface DistributorConfig extends Accessible {

  @DefaultValue("1")
  @Key("distributor.service.threads")
  int getServiceThreadCount();
}
