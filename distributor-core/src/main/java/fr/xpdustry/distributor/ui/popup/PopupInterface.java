package fr.xpdustry.distributor.ui.popup;

import arc.util.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.serializer.*;
import fr.xpdustry.distributor.ui.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public class PopupInterface implements Interface<PopupPane> {

  private final int updateInterval;

  public PopupInterface(final int updateInterval) {
    this.updateInterval = updateInterval;
  }

  @Override
  public CloseableView<PopupPane> open(@NotNull Player viewer, @NotNull MetadataContainer metadata) {
    return null;
  }

  @Override
  public CloseableView<PopupPane> open(@NotNull Player viewer) {
    return open(viewer, MetadataContainer.empty());
  }

  private final class PopupView implements View<PopupPane> {

    private final PopupPane pane;
    private final MetadataContainer metadata;
    private final Player viewer;
    private final Timer.Task updater;

    private PopupView(PopupPane pane, MetadataContainer metadata, Player viewer) {
      this.pane = pane;
      this.metadata = metadata;
      this.viewer = viewer;
      this.updater = Timer.schedule(() -> {
        Call.infoPopup(
          PopupView.this.viewer.con(),
          ComponentSerializer.client().serialize(pane.getContent(), MetadataProvider.empty()),
          updateInterval,
          0,
          0,
          0,
          0,
          0
        );
      }, 0, updateInterval);
    }

    @Override
    public PopupPane getPane() {
      return pane;
    }

    @Override
    public MetadataContainer getMetadata() {
      return metadata;
    }

    @Override
    public Interface<PopupPane> getInterface() {
      return PopupInterface.this;
    }

    @Override
    public Player getViewer() {
      return viewer;
    }

    @Override
    public boolean isViewing() {
      return updater.isScheduled();
    }
  }
}
