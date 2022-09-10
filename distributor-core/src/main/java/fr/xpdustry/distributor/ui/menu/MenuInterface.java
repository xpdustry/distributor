package fr.xpdustry.distributor.ui.menu;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.serializer.*;
import fr.xpdustry.distributor.ui.*;
import java.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import org.jetbrains.annotations.*;

public final class MenuInterface implements Interface<MenuPane> {

  private final Action<MenuPane> closeHandler;
  private final List<Transformer<MenuPane>> transformers;

  private final int id;
  private final Map<Player, MenuView> views = new HashMap<>();

  public MenuInterface(final Action<MenuPane> closeHandler, List<Transformer<MenuPane>> transformers) {
    this.closeHandler = closeHandler;
    this.transformers = transformers;
    this.id = Menus.registerMenu((player, option) -> {
      final var view = MenuInterface.this.views.remove(player);
      if (view == null) {
        throw new IllegalStateException();
      } else if (option == -1) {
        MenuInterface.this.closeHandler.accept(view);
      } else {
        view.getPane().getOption(option).getAction().accept(view);
      }
    });
  }

  @Override
  public View<MenuPane> open(@NotNull Player viewer, @NotNull MetadataContainer metadata) {
    return views.computeIfAbsent(viewer, p -> {
      final var audience = DistributorPlugin.getAudienceProvider().player(viewer);
      final var serializer = ComponentSerializer.client();

      var pane = new MenuPane();
      for (final var transformer : transformers) {
        pane = transformer.transform(p, metadata, pane);
      }

      Call.menu(
        id,
        serializer.serialize(pane.getTitle(), audience),
        serializer.serialize(pane.getContent(), audience),
        Arrays.stream(pane.options)
          .map(r -> Arrays.stream(r)
            .map(o -> serializer.serialize(o.getContent(), audience))
            .toArray(String[]::new)
          )
          .toArray(String[][]::new)
      );

      return new MenuView(pane, metadata, p);
    });
  }

  private final class MenuView implements View<MenuPane> {

    private final MenuPane pane;
    private final MetadataContainer metadata;
    private final Player viewer;

    private MenuView(MenuPane pane, MetadataContainer metadata, Player viewer) {
      this.pane = pane;
      this.metadata = metadata;
      this.viewer = viewer;
    }

    @Override
    public Interface<MenuPane> getInterface() {
      return MenuInterface.this;
    }

    @Override
    public MenuPane getPane() {
      return pane;
    }

    @Override
    public MetadataContainer getMetadata() {
      return metadata;
    }

    @Override
    public Player getViewer() {
      return viewer;
    }

    @Override
    public boolean isViewing() {
      return views.containsKey(viewer);
    }
  }
}
