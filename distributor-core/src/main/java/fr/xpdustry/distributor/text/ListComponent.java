package fr.xpdustry.distributor.text;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public final class ListComponent extends Component {

  private final List<Component> components;

  ListComponent(final List<Component> components, final @Nullable TextColor color, final Iterable<TextDecoration> decorations) {
    super(color, decorations);
    this.components = List.copyOf(components);
  }

  public List<Component> getComponents() {
    return components;
  }

  @Override
  public boolean isEmpty() {
    return components.isEmpty();
  }

  public static final class Builder extends Component.Builder<ListComponent, Builder> {

    private List<Component> components = new ArrayList<>();

    public Builder components(final Component... components) {
      this.components = List.of(components);
      return this;
    }

    public Builder components(final List<Component> components) {
      this.components = components;
      return this;
    }

    public List<Component> components() {
      return Collections.unmodifiableList(components);
    }

    public Builder add(final Component component) {
      this.components.add(component);
      return this;
    }

    @Override
    public Builder from(final ListComponent component) {
      this.components(component.components);
      return super.from(component);
    }

    @Override
    public ListComponent build() {
      return new ListComponent(components, color(), decorations());
    }
  }
}
