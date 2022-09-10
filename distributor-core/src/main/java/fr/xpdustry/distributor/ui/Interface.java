package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public interface Interface<P extends Pane> {

  View<P> open(final @NotNull Player viewer, final @NotNull MetadataContainer metadata);

  default View<P> open(final @NotNull Player viewer) {
    return open(viewer, MetadataContainer.empty());
  }

  List<Transformer<P>> getTransformers();
}
