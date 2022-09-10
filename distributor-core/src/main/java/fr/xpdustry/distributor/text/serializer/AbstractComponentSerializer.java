package fr.xpdustry.distributor.text.serializer;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.format.*;
import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public abstract class AbstractComponentSerializer implements ComponentSerializer<String> {

  @Override
  public String serialize(final Component component, MetadataContainer metadata) {
    final var context = new Context();
    render(component, context, metadata);
    return context.toString();
  }

  private void render(final Component component, final Context context, MetadataContainer metadata) {
    if (component.isEmpty()) {
      return;
    }

    context.stack.push(TextStyle.empty());
    startStyle(context);

    if (component instanceof TextComponent text) {
      appendText(context, text.getContent());
    } else if (component instanceof ListComponent list) {
      list.getComponents().forEach(element -> render(element, context, metadata));
    } else if (component instanceof TranslatableComponent translatable) {
      final var locale = metadata.getMetadata(StandardKeys.LOCALE)
        .orElseGet(Locale::getDefault);
      var text = DistributorPlugin.getGlobalTranslator()
        .translate(translatable.getKey(), locale);
      if (text == null) {
        text = "???" + translatable.getKey() + "???";
      }
      final var arguments = translatable.getArguments()
        .stream()
        .map(c -> serialize(c, metadata))
        .toArray();
      appendText(context, String.format(locale, text, arguments));
    } else {
      throw new IllegalArgumentException("Unknown component type: " + component.getClass().getName());
    }

    closeStyle(context);
    context.stack.pop();
  }

  protected void appendText(Context context, String text) {
    context.appendText(text);
  }

  protected abstract void startStyle(Context context);

  protected abstract void closeStyle(Context context);

  protected static final class Context {

    private final StringBuilder builder = new StringBuilder(256);
    private final Deque<TextStyle> stack = new ArrayDeque<>();

    public void appendText(final String text) {
      builder.append(text);
    }

    public @Nullable TextColor getCurrentColor() {
      final var iterator = stack.descendingIterator();
      while (iterator.hasNext()) {
        final var element = iterator.next();
        if (element.getColor() != null) {
          return element.getColor();
        }
      }
      return null;
    }

    public @Nullable TextColor getPreviousColor() {
      final var iterator = stack.descendingIterator();
      if (iterator.hasNext()) {
        iterator.next();
      }
      while (iterator.hasNext()) {
        final var element = iterator.next();
        if (element.getColor() != null) {
          return element.getColor();
        }
      }
      return null;
    }

    public Set<TextDecoration> getCurrentDecorations() {
      final var decorations = EnumSet.noneOf(TextDecoration.class);
      stack.forEach(style -> decorations.addAll(style.getDecorations()));
      return decorations;
    }

    public Set<TextDecoration> getPreviousDecorations() {
      final var decorations = EnumSet.noneOf(TextDecoration.class);
      final var iterator = stack.descendingIterator();
      if (iterator.hasNext()) {
        iterator.next();
      }
      iterator.forEachRemaining(style -> decorations.addAll(style.getDecorations()));
      return decorations;
    }

    public boolean isPreviousColorDifferentFromCurrent() {
      TextColor first = null;
      final var iterator = stack.descendingIterator();
      while (iterator.hasNext()) {
        final var element = iterator.next().getColor();
        if (element != null) {
          if (first == null) {
            first = element;
          } else {
            return !first.equals(element);
          }
        }
      }
      return first != null;
    }

    @Override
    public String toString() {
      return builder.toString();
    }
  }
}
