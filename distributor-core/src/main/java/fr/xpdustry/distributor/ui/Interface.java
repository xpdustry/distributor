package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.metadata.*;
import java.util.*;
import mindustry.gen.*;

public interface Interface {

  default void show(Player player) {
    show(player, MetadataProvider.empty());
  }

  void show(Player player, MetadataProvider metadata);

  Collection<Player> getViewers();
}
