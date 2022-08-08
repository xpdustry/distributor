package fr.xpdustry.distributor.text.serializer;

import fr.xpdustry.distributor.text.format.*;
import java.util.*;

final class ServerComponentSerializer extends AbstractComponentSerializer {

  static final ServerComponentSerializer INSTANCE = new ServerComponentSerializer();

  private ServerComponentSerializer() {
  }

  @SuppressWarnings({"ConstantConditions", "NullAway"})
  @Override
  protected void startStyle(Context context) {
    if (context.isPreviousColorDifferentFromCurrent()) {
      appendColor(context, context.getCurrentColor());
    }

    final var decorations = context.getCurrentDecorations();
    decorations.removeAll(context.getPreviousDecorations());
    appendDecorations(context, decorations);
  }

  @Override
  protected void closeStyle(Context context) {
    var hadReset = false;

    if (context.isPreviousColorDifferentFromCurrent()) {
      appendReset(context);
      hadReset = true;
      if (context.getPreviousColor() != null) {
        appendColor(context, context.getPreviousColor());
      }
    }

    if (!context.getPreviousDecorations().equals(context.getCurrentDecorations())) {
      if (!hadReset) {
        appendReset(context);
      }
      appendDecorations(context, context.getPreviousDecorations());
    }
  }

  private void appendColor(Context context, final TextColor color) {
    context.appendText("\u001b[38;2;%d;%d;%dm".formatted(color.getR(), color.getG(), color.getB()));
  }

  private void appendDecorations(Context context, final Set<TextDecoration> decorations) {
    for (final var decoration : decorations) {
      final var code = switch (decoration) {
        case ITALIC -> "\u001b[3m";
        case BOLD -> "\u001b[1m";
        case UNDERLINE -> "\u001b[4m";
      };
      context.appendText(code);
    }
  }

  private void appendReset(Context context) {
    context.appendText("\u001b[0m");
  }
}
