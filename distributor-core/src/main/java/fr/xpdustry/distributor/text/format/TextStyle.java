package fr.xpdustry.distributor.text.format;

import java.util.*;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class TextStyle {

  private static final TextStyle EMPTY = new TextStyle(null, Collections.emptySet());

  private final @Nullable TextColor color;
  private final Set<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);

  public static TextStyle empty() {
    return EMPTY;
  }

  public static TextStyle of(final @Nullable TextColor color, final TextDecoration... decorations) {
    return new TextStyle(color, List.of(decorations));
  }

  public static TextStyle of(final @Nullable TextColor color, final Iterable<TextDecoration> decorations) {
    return new TextStyle(color, decorations);
  }

  public static TextStyle of(final @Nullable TextColor color) {
    return new TextStyle(color, Collections.emptyList());
  }

  public static TextStyle of(final TextDecoration... decorations) {
    return new TextStyle(null, List.of(decorations));
  }

  public static TextStyle of(final Iterable<TextDecoration> decorations) {
    return new TextStyle(null, decorations);
  }

  private TextStyle(final @Nullable TextColor color, final Iterable<TextDecoration> decorations) {
    this.color = color;
    decorations.forEach(this.decorations::add);
  }

  public @Nullable TextColor getColor() {
    return color;
  }

  public Set<TextDecoration> getDecorations() {
    return Collections.unmodifiableSet(decorations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, decorations);
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || (
      obj instanceof TextStyle style
        && Objects.equals(this.color, style.color)
        && this.decorations.equals(style.decorations)
    );
  }
}