package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
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
    return new TextComponent(content, TextStyle.empty());
  }

  public static TextComponent text(final String content, final @Nullable TextColor color) {
    return new TextComponent(content, TextStyle.of(color));
  }

  public static TextComponent text(final String content, final TextDecoration... decorations) {
    return new TextComponent(content, TextStyle.of(decorations));
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

  public static ListComponent list(final List<Component> components, final @Nullable TextColor color) {
    return new ListComponent(components, TextStyle.of(color));
  }

  public static ListComponent list(final List<Component> components, final TextDecoration... decorations) {
    return new ListComponent(components, TextStyle.of(decorations));
  }

  public static ListComponent list(final List<Component> components, final TextStyle style) {
    return new ListComponent(components, style);
  }

  public static TranslatableComponent.Builder translatable() {
    return new TranslatableComponent.Builder();
  }

  public static TranslatableComponent translatable(final String key, final @Nullable TextColor color) {
    return new TranslatableComponent(key, Collections.emptyList(), TextStyle.of(color));
  }

  public static TranslatableComponent translatable(final String key, final TextDecoration... decorations) {
    return new TranslatableComponent(key, Collections.emptyList(), TextStyle.of(decorations));
  }

  public static TranslatableComponent translatable(final String key, final TextStyle style) {
    return new TranslatableComponent(key, Collections.emptyList(), style);
  }

  public static TranslatableComponent translatable(final String key, final List<Component> arguments, final @Nullable TextColor color) {
    return new TranslatableComponent(key, arguments, TextStyle.of(color));
  }

  public static TranslatableComponent translatable(final String key, final List<Component> arguments, final TextDecoration... decorations) {
    return new TranslatableComponent(key, arguments, TextStyle.of(decorations));
  }

  public static TranslatableComponent translatable(final String key, final List<Component> arguments, TextStyle style) {
    return new TranslatableComponent(key, arguments, style);
  }
}
