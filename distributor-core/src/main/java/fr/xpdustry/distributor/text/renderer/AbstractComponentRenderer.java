package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.*;
import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public abstract class AbstractComponentRenderer implements ComponentRenderer {

  private static final Style EMPTY = new Style(null, Collections.emptySet());

  @Override
  public String render(final Component component) {
    final var builder = new StringBuilder();
    render(component, new ArrayDeque<>(), builder);
    return builder.toString();
  }

  private void render(final Component component, final Deque<Style> stack, final StringBuilder builder) {
    if (component.isEmpty()) {
      return;
    }

    final var style = new Style(component.getColor(), component.getDecorations());
    startStyle(builder, style);

    // Java 17 black magik pls
    if (component instanceof TextComponent text) {
      builder.append(text.getContent());
    } else if (component instanceof ListComponent list) {
      stack.push(style);
      list.getComponents().forEach(c -> render(component, stack, builder));
      stack.pop();
    }

    closeStyle(builder, style, stack.isEmpty() ? EMPTY : stack.peek());
  }

  protected void appendText(final StringBuilder builder, final String text) {
    builder.append(text);
  }

  protected abstract void startStyle(final StringBuilder builder, final Style style);

  protected abstract void closeStyle(final StringBuilder builder, final Style style, final Style last);

  protected static final class Style {

    private final @Nullable TextColor color;
    private final Set<TextDecoration> decorations;

    private Style(@Nullable TextColor color, Set<TextDecoration> decorations) {
      this.color = color;
      this.decorations = decorations;
    }

    public @Nullable TextColor getColor() {
      return color;
    }

    public Set<TextDecoration> getDecorations() {
      return decorations;
    }
  }
}
