package fr.xpdustry.distributor.text;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public abstract sealed class Component permits ListComponent, TextComponent {

  private final @Nullable TextColor color;
  private final Set<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);

  protected Component(final @Nullable TextColor color, final Iterable<TextDecoration> decorations) {
    this.color = color;
    decorations.forEach(this.decorations::add);
  }

  public @Nullable TextColor getColor() {
    return color;
  }

  public Set<TextDecoration> getDecorations() {
    return Collections.unmodifiableSet(decorations);
  }

  public Component joins(final Iterable<Component> components) {
    final var iterator = components.iterator();
    if (!iterator.hasNext()) {
      return Components.empty();
    }
    final var builder = Components.list().add(iterator.next());
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

  public abstract static class Builder<C extends Component, B extends Builder<C, B>> {

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

    public B from(final C component) {
      this.color(component.getColor());
      this.decorations(component.getDecorations());
      return self();
    }

    public abstract C build();

    @SuppressWarnings("unchecked")
    protected B self() {
      return (B) this;
    }
  }
}
