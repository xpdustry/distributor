package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.distributor.util.Buildable.*;
import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public abstract class ComponentBuilder<C extends Component&Buildable<C, B>, B extends ComponentBuilder<C, B>> implements Buildable.Builder<C> {

  private TextStyle style = TextStyle.empty();

  @SuppressWarnings("unchecked")
  public final B withStyle(final TextStyle style) {
    this.style = style;
    return (B) this;
  }

  public final TextStyle getStyle() {
    return style;
  }

  public final B withColor(final @Nullable TextColor color) {
    return withStyle(TextStyle.of(color, getDecorations()));
  }

  public final @Nullable TextColor getColor() {
    return style.getColor();
  }

  public final B withDecorations(final TextDecoration... decorations) {
    return withStyle(TextStyle.of(getColor(), List.of(decorations)));
  }

  public final B withDecorations(final Set<TextDecoration> decorations) {
    return withStyle(TextStyle.of(getColor(), decorations));
  }

  public final Set<TextDecoration> getDecorations() {
    return this.style.getDecorations();
  }
}
