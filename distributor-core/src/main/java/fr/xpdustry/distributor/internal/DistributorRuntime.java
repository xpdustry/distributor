package fr.xpdustry.distributor.internal;

import fr.xpdustry.distributor.message.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public interface DistributorRuntime {

  default @NotNull MediaReceiver createPlayerMediaReceiverDelegate(final @NotNull Player player) {
    return new PlayerMediaReceiver(player);
  }
}
