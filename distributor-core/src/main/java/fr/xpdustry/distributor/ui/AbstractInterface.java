package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.event.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public abstract class AbstractInterface<P extends Pane> implements Interface<P>, EventListener {

  private final List<Transformer<P>> transformers;
  private final Map<Player, View<P>> viewers = new HashMap<>();

  protected AbstractInterface(final List<Transformer<P>> transformers) {
    this.transformers = transformers;
  }

  @Override
  public View<P> open(final @NotNull Player viewer, final @NotNull MetadataContainer metadata) {
    return viewers.computeIfAbsent(viewer, v -> {
      final var view = new SimpleView(viewer, metadata);
      for (final var transformer : transformers) {
        view.pane = transformer.apply(view);
      }
      onViewOpen(view);
      return view;
    });
  }

  @EventHandler
  void onPlayerLeave(final EventType.PlayerLeave event) {
    if (viewers.containsKey(event.player)) {
      onViewHide(viewers.remove(event.player));
    }
  }

  protected abstract P createEmptyPane();

  protected abstract void onViewOpen(final View<P> view);

  protected abstract void onViewHide(final View<P> view);

  private final class SimpleView implements View<P> {

    private final Player viewer;
    private final MetadataContainer metadata;
    private P pane = createEmptyPane();

    private SimpleView(final Player viewer, final MetadataContainer metadata) {
      this.viewer = viewer;
      this.metadata = metadata;
    }

    @Override
    public Player getViewer() {
      return viewer;
    }

    @Override
    public MetadataContainer getMetadata() {
      return metadata;
    }

    @Override
    public Interface<P> getInterface() {
      return AbstractInterface.this;
    }

    @Override
    public P getPane() {
      return pane;
    }

    @Override
    public boolean isViewing() {
      return viewers.containsKey(viewer);
    }
  }

  public static abstract class Builder<B extends Builder<B, P>, P extends Pane> implements Buildable.Builder<B> {

    private final List<Transformer<P>> transformers = new ArrayList<>();

    public B addTransformer(final Transformer<P> transformer) {
      this.transformers.add(transformer);
      return (B) this;
    }
  }
}
