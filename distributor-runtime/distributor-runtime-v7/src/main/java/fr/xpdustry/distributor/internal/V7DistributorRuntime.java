package fr.xpdustry.distributor.internal;

import fr.xpdustry.distributor.message.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public final class V7DistributorRuntime implements DistributorRuntime {

  @Override
  public @NotNull MediaReceiver createPlayerMediaReceiverDelegate(final @NotNull Player player) {
    return new V7PlayerMediaReceiver(player);
  }
}
