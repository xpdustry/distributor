package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import java.util.*;

public final class ListComponent extends Component {

  private final List<Component> components;

  ListComponent(final List<Component> components, final TextStyle style) {
    super(style);
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
      return new ListComponent(components, style());
    }
  }
}
