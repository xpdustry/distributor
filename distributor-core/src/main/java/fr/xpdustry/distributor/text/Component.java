package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import java.util.*;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract sealed class Component permits ListComponent, TextComponent {

  private final TextStyle style;

  protected Component(final TextStyle style) {
    this.style = style;
  }

  public final TextStyle getStyle() {
    return style;
  }

  public Component joins(final Iterable<Component> components) {
    final var iterator = components.iterator();
    if (!iterator.hasNext()) {
      return Components.empty();
    }
    final var builder = Components
      .list()
      .add(iterator.next());
    while (iterator.hasNext()) {
      builder.add(this).add(iterator.next());
    }
    return builder.build();
  }

  public Component joins(final Component... components) {
    return joins(List.of(components));
  }

  public Component repeat(final int times) {
    if (times <= 0 || isEmpty()) {
      return Components.empty();
    }
    final var builder = Components.list();
    for (int i = 0; i < times; i++) {
      builder.add(this);
    }
    return builder.build();
  }

  public abstract boolean isEmpty();

  public static abstract class Builder<C extends Component, B extends Builder<C, B>> {

    private @Nullable TextColor color = null;
    private Set<TextDecoration> decorations = Collections.emptySet();

    public final B color(final @Nullable TextColor color) {
      this.color = color;
      return self();
    }

    public final @Nullable TextColor color() {
      return this.color;
    }

    public final B decorations(final TextDecoration... decorations) {
      this.decorations = Set.of(decorations);
      return self();
    }

    public final B decorations(final Set<TextDecoration> decorations) {
      this.decorations = new HashSet<>(decorations);
      return self();
    }

    public final Set<TextDecoration> decorations() {
      return Collections.unmodifiableSet(decorations);
    }

    public final B style(final TextStyle style) {
      this.color = style.getColor();
      this.decorations = style.getDecorations();
      return self();
    }

    public final TextStyle style() {
      return TextStyle.of(color, decorations);
    }

    public B from(final C component) {
      this.style(component.getStyle());
      return self();
    }

    public abstract C build();

    @SuppressWarnings("unchecked")
    protected B self() {
      return (B) this;
    }
  }
}
