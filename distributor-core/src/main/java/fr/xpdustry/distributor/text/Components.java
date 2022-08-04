package fr.xpdustry.distributor.text;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public final class Components {

  private Components() {
  }

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
    return new TextComponent(content, null, Collections.emptyList());
  }

  public static TextComponent text(final String content, final @Nullable TextColor color, final TextDecoration... decorations) {
    return new TextComponent(content, color, List.of(decorations));
  }

  public static TextComponent text(final String content, final TextDecoration... decorations) {
    return new TextComponent(content, null, List.of(decorations));
  }

  public static ListComponent.Builder list() {
    return new ListComponent.Builder();
  }

  public static ListComponent list(final Component... components) {
    return new ListComponent(List.of(components), null, Collections.emptyList());
  }

  public static ListComponent list(final List<Component> components, final @Nullable TextColor color, final TextDecoration... decorations) {
    return new ListComponent(components, color, List.of(decorations));
  }

  public static ListComponent list(final List<Component> components, final TextDecoration... decorations) {
    return new ListComponent(components, null, List.of(decorations));
  }
}
