package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;

public final class ListComponent extends Component implements Buildable<ListComponent, ListComponent.Builder> {

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

  @Override
  public Component append(Component component) {
    if (component.isEmpty()) {
      return this;
    } else {
      return toBuilder().add(component).build();
    }
  }

  @Override
  public Builder toBuilder() {
    return new Builder().withStyle(getStyle()).withComponents(getComponents());
  }

  public static final class Builder extends ComponentBuilder<ListComponent, Builder> {

    private final List<Component> components = new ArrayList<>();

    Builder() {
    }

    public Builder add(final Component component) {
      this.components.add(component);
      return this;
    }

    public Builder withComponents(final Component... components) {
      return withComponents(Arrays.asList(components));
    }

    public Builder withComponents(final List<Component> components) {
      this.components.clear();
      this.components.addAll(components);
      return this;
    }

    public List<Component> getComponents() {
      return Collections.unmodifiableList(components);
    }

    @Override
    public ListComponent build() {
      return new ListComponent(components, getStyle());
    }
  }

  @Override
  public String toString() {
    return components.toString();
  }
}
