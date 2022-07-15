package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.TextColor;
import fr.xpdustry.distributor.text.format.TextDecoration;
import fr.xpdustry.distributor.text.format.TextStyle;
import java.util.*;
import org.checkerframework.checker.nullness.qual.Nullable;

// TODO Add more default constructors
public abstract sealed class Component permits ListComponent, TextComponent {

  private final TextStyle style;

  public static ListComponent.Builder builder() {
    return new ListComponent.Builder();
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
    return new TextComponent(content, TextStyle.of(null, Collections.emptySet()));
  }

  public static TextComponent text(final String content, final TextDecoration... decorations) {
    return new TextComponent(content, TextStyle.of(null, Set.of(decorations)));
  }

  public static TextComponent text(final String content, final TextColor color, final TextDecoration... decorations) {
    return new TextComponent(content, TextStyle.of(color, Set.of(decorations)));
  }

  protected Component(final TextStyle style) {
    this.style = style;
  }

  public final TextStyle getStyle() {
    return style;
  }

  public final ListComponent joins(final Component... components) {
    final var builder = builder();
    if (components.length != 0) {
      builder.add(components[0]);
    }
    for (int i = 1; i < components.length; i++) {
      builder.add(this).add(components[i]);
    }
    return builder.build();
  }

  public static abstract class Builder<C extends Component, B extends Builder<C, B>> {

    private @Nullable TextColor color = null;
    private Set<TextDecoration> decorations = Collections.emptySet();

    @SuppressWarnings("unchecked")
    public final B color(final @Nullable TextColor color) {
      this.color = color;
      return (B) this;
    }

    public final @Nullable TextColor color() {
      return this.color;
    }

    @SuppressWarnings("unchecked")
    public final B decorations(final TextDecoration... decorations) {
      this.decorations = Set.of(decorations);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B decorations(final Set<TextDecoration> decorations) {
      this.decorations = new HashSet<>(decorations);
      return (B) this;
    }

    public final Set<TextDecoration> decorations() {
      return Collections.unmodifiableSet(decorations);
    }

    @SuppressWarnings("unchecked")
    public final B style(final TextStyle style) {
      this.color = style.getColor();
      this.decorations = style.getDecorations();
      return (B) this;
    }

    public final TextStyle style() {
      return TextStyle.of(color, decorations);
    }

    public abstract C build();
  }
}
