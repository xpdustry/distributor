package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.data.*;
import java.util.function.*;
import mindustry.gen.*;

public interface InterfaceView {

  interface Factory<V extends InterfaceView> extends BiFunction<Player, MetadataProvider, V> {

    @Override
    V apply(final Player player, final MetadataProvider metadata);
  }
}
