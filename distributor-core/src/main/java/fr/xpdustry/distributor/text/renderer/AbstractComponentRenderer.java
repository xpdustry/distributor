package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.format.TextStyle;
import fr.xpdustry.distributor.text.Component;
import fr.xpdustry.distributor.text.ListComponent;
import fr.xpdustry.distributor.text.TextComponent;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractComponentRenderer<B> implements ComponentRenderer {

  @Override
  public String render(Component component) {
    final var builder = createBuilder();
    render(component, new ArrayDeque<>(), builder);
    return builder.toString();
  }

  private void render(Component component, Deque<TextStyle> stack, B builder) {
    /* TODO Move optimizations and component checks
    if (component instanceof TextComponent text && text.getContent().isEmpty()) {
      return;
    }
     */

    startStyle(builder, component.getStyle());

    if (component instanceof TextComponent text) {
      appendText(builder, text.getContent());
    } else if (component instanceof ListComponent list) {
      if (!list.getComponents().isEmpty()) {
        stack.push(component.getStyle());
        list.getComponents().forEach(c -> render(c, stack, builder));
        stack.pop();
      }
    } else {
      throw new IllegalStateException(
        "An unknown component has been encountered: " + component.getClass()
      );
    }

    closeStyle(builder, stack.isEmpty() ? TextStyle.empty() : stack.peek());
  }

  protected abstract B createBuilder();

  protected abstract void appendText(B builder, String text);

  protected abstract void startStyle(B builder, TextStyle style);

  protected abstract void closeStyle(B builder, TextStyle last);
}
