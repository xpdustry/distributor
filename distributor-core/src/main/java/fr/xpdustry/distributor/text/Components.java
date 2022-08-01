package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import java.util.*;

public final class Components {

  public static TextComponent empty() {
    return TextComponent.EMPTY;
  }

  public static TextComponent newline() {
    return TextComponent.NEWLINE;
  }

  public static TextComponent space() {
    return TextComponent.SPACE;
  }

  public static TextComponent.Builder text() {
    return new TextComponent.Builder();
  }

  public static TextComponent text(final String content) {
    return new TextComponent(content, TextStyle.empty());
  }

  public static TextComponent text(final String content, final TextDecoration... decorations) {
    return new TextComponent(content, TextStyle.of(null, List.of(decorations)));
  }

  public static TextComponent text(final String content, final TextColor color, final TextDecoration... decorations) {
    return new TextComponent(content, TextStyle.of(color, List.of(decorations)));
  }

  public static TextComponent text(final String content, final TextStyle style) {
    return new TextComponent(content, style);
  }

  public static ListComponent.Builder list() {
    return new ListComponent.Builder();
  }

  public static ListComponent list(final Component... components) {
    return new ListComponent(List.of(components), TextStyle.empty());
  }

  public static ListComponent list(final List<Component> components) {
    return new ListComponent(components, TextStyle.empty());
  }

  private Components() {
  }
}
