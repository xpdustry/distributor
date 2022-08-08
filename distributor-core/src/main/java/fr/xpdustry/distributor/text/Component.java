package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import java.util.*;

public abstract sealed class Component permits ListComponent, TextComponent, TranslatableComponent {

  private final TextStyle style;

  protected Component(final TextStyle style) {
    this.style = style;
  }

  public TextStyle getStyle() {
    return this.style;
  }

  public Component join(final Iterable<Component> components) {
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

  public Component join(final Component... components) {
    return join(List.of(components));
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

  public Component append(final Component component) {
    if (component.isEmpty()) {
      return this;
    } else {
      return new ListComponent(List.of(this, component), getStyle());
    }
  }

  public abstract boolean isEmpty();
}
