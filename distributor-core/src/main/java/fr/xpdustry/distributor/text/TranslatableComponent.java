package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import org.jetbrains.annotations.*;

public final class TranslatableComponent extends Component implements Buildable<TranslatableComponent, TranslatableComponent.Builder> {

  private final String key;
  private final List<Component> arguments;

  TranslatableComponent(final String key, List<Component> arguments, TextStyle style) {
    super(style);
    this.key = key;
    this.arguments = List.copyOf(arguments);
  }

  public String getKey() {
    return key;
  }

  public List<Component> getArguments() {
    return arguments;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new Builder().withStyle(getStyle()).withKey(getKey()).withArguments(getArguments());
  }

  public static final class Builder extends ComponentBuilder<TranslatableComponent, Builder> {

    private String key = "";
    private final List<Component> arguments = new ArrayList<>();

    Builder() {
    }

    public Builder withKey(final String key) {
      this.key = key;
      return this;
    }

    public String getKey() {
      return key;
    }

    public Builder withArguments(final Component... components) {
      return withArguments(Arrays.asList(components));
    }

    public Builder withArguments(final List<Component> components) {
      this.arguments.clear();
      this.arguments.addAll(components);
      return this;
    }

    public List<Component> getArguments() {
      return Collections.unmodifiableList(this.arguments);
    }

    @Override
    public @NotNull TranslatableComponent build() {
      return new TranslatableComponent(key, arguments, getStyle());
    }
  }
}
