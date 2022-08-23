package fr.xpdustry.distributor.ui;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.serializer.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import mindustry.gen.*;
import mindustry.ui.*;

public final class MenuInterface implements Interface {

  private final InterfaceAction<MenuInterface> closeHandler;
  private final InterfaceView.Factory<MenuView> viewFactory;

  private final int id;
  private final Map<Player, MenuView> views = new HashMap<>();

  public MenuInterface(final InterfaceAction<MenuInterface> closeHandler, final InterfaceView.Factory<MenuView> viewFactory) {
    this.closeHandler = closeHandler;
    this.viewFactory = viewFactory;
    this.id = Menus.registerMenu((player, option) -> {
      final var view = MenuInterface.this.views.remove(player);
      if (view == null) {
        // TODO Better message :)
        player.sendMessage("The menu has timed out.");
      } else if (option == -1) {
        MenuInterface.this.closeHandler.accept(player, MenuInterface.this);
      } else {
        view.getOption(option).orElseThrow().action.accept(player, MenuInterface.this);
      }
    });
  }

  @Override
  public void show(Player player, MetadataProvider metadata) {
    if (views.containsKey(player)) {
      return;
    }

    final var view = this.viewFactory.apply(player, metadata);
    final var audience = DistributorPlugin.getAudienceProvider().player(player);
    final var serializer = ComponentSerializer.client();

    final var options = new String[view.options.size()][];
    for (int row = 0; row < view.options.size(); row++) {
      options[row] = view.options.get(row).stream()
        .map(o -> serializer.serialize(o.content, audience))
        .toArray(String[]::new);
    }

    views.put(player, view);
    Call.menu(
      id,
      serializer.serialize(view.getTitle(), audience),
      serializer.serialize(view.getContent(), audience),
      options
    );
  }

  @Override
  public Collection<Player> getViewers() {
    return Collections.unmodifiableCollection(views.keySet());
  }

  public static final class Option {

    private final Component content;
    private final InterfaceAction<MenuInterface> action;

    public Option(final Component content, final InterfaceAction<MenuInterface> action) {
      this.content = content;
      this.action = action;
    }

    public Component getContent() {
      return content;
    }

    public InterfaceAction<MenuInterface> getAction() {
      return action;
    }
  }

  public static final class MenuView implements InterfaceView {

    private final Component title;
    private final Component content;
    private final List<List<Option>> options;

    public static MenuView.Builder builder() {
      return new MenuView.Builder();
    }

    private MenuView(Component title, Component content, List<List<Option>> options) {
      this.title = title;
      this.content = content;
      this.options = List.copyOf(options);
    }

    public Component getTitle() {
      return title;
    }

    public Component getContent() {
      return content;
    }

    public List<List<Option>> getOptions() {
      return options;
    }

    public Optional<Option> getOption(final int id) {
      return options.stream()
        .flatMap(Collection::stream)
        .skip(id)
        .findFirst();
    }

    public static final class Builder implements Buildable.Builder<MenuView> {

      private final List<List<Option>> options = new ArrayList<>();
      private Component title = Components.empty();
      private Component content = Components.empty();

      private Builder() {
      }

      public Builder withTitle(final Component title) {
        this.title = title;
        return this;
      }

      public Builder withContent(final Component content) {
        this.content = content;
        return this;
      }

      public Builder addRow(Option... options) {
        this.options.add(List.of(options));
        return this;
      }

      @Override
      public MenuView build() {
        return new MenuView(title, content, options);
      }
    }
  }

  /*
  public static final class Builder implements Buildable.Builder<MenuInterface> {

    private InterfaceAction<MenuInterface> closeHandler = InterfaceAction.nothing();
    private InterfaceView.Factory<MenuView> viewFactory = (p, m) -> MenuView.builder().build();

    public Builder withCloseHandler(final InterfaceAction<MenuInterface> closeHandler) {
      this.closeHandler = closeHandler;
      return this;
    }

    public Builder withViewProvider(final InterfaceView.Factory<MenuView> viewFactory) {
      this.viewFactory = viewFactory;
      return this;
    }

    @Override
    public MenuInterface build() {
      return new MenuInterface(closeHandler, viewFactory);
    }
  }
   */
}
