package fr.xpdustry.distributor.ui.menu;

import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.ui.*;

public final class MenuOption {

  private static final MenuOption EMPTY = new MenuOption(Components.empty(), Action.none());

  private final Component content;
  private final Action<MenuPane> action;

  public static MenuOption empty() {
    return EMPTY;
  }

  public static MenuOption of(final Component content, final Action<MenuPane> action) {
    return new MenuOption(content, action);
  }

  private MenuOption(final Component content, final Action<MenuPane> action) {
    this.content = content;
    this.action = action;
  }

  public Component getContent() {
    return content;
  }

  public Action<MenuPane> getAction() {
    return action;
  }
}
