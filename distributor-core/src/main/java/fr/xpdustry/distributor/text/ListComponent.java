package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.TextStyle;
import java.util.*;

public final class ListComponent extends Component {

  private final List<Component> components;

  private ListComponent(final List<Component> components, final TextStyle style) {
    super(style);
    this.components = components;
  }

  public List<Component> getComponents() {
    return Collections.unmodifiableList(components);
  }

  public static final class Builder extends Component.Builder<ListComponent, Builder> {

    private final List<Component> components = new ArrayList<>();

    public Builder components(final List<Component> components) {
      this.components.clear();
      this.components.addAll(components);
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
    public ListComponent build() {
      return new ListComponent(components, style());
    }
  }
}
