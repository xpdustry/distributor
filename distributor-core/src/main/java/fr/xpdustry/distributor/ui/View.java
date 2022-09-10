package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.metadata.*;
import mindustry.gen.*;

public interface View<P extends Pane> {

  P getPane();

  MetadataContainer getMetadata();

  Interface<P> getInterface();

  Player getViewer();

  boolean isViewing();

  void open();
}
