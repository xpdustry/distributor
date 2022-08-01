package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.text.*;
import java.util.*;

public abstract class AbstractComponentRenderer implements ComponentRenderer {

  @Override
  public String render(final Component component) {
    final var builder = new StringBuilder();
    render(component, new ArrayDeque<>(), builder);
    return builder.toString();
  }

  private void render(final Component component, final Deque<TextStyle> stack, final StringBuilder builder) {
    if (component.isEmpty()) {
      return;
    }

    startStyle(builder, component.getStyle());

    switch (component) {
      case TextComponent text -> {
        builder.append(text);
      }
      case ListComponent list -> {
        list.getComponents().forEach(c -> render(component, stack, builder));
      }
    }

    closeStyle(builder, component.getStyle(), stack.isEmpty() ? TextStyle.empty() : stack.peek());
  }

  protected void appendText(final StringBuilder builder, final String text) {
    builder.append(text);
  }

  protected abstract void startStyle(final StringBuilder builder, final TextStyle style);

  protected abstract void closeStyle(final StringBuilder builder, final TextStyle style, final TextStyle last);
}
