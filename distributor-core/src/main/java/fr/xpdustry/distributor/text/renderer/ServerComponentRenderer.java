package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.format.*;

final class ServerComponentRenderer extends AbstractComponentRenderer {

  private static final String CONSOLE_COLOR = "\u001b[38;2;%d;%d;%dm";

  static final ServerComponentRenderer INSTANCE = new ServerComponentRenderer();

  private ServerComponentRenderer() {
  }

  @Override
  protected void startStyle(final StringBuilder builder, final TextStyle style) {
    final var color = style.getColor();
    if (color != null) {
      builder.append(CONSOLE_COLOR.formatted(color.getR(), color.getG(), color.getB()));
    }
    for (final var decoration : style.getDecorations()) {
      builder.append(switch (decoration) {
        case ITALIC     -> "\u001b[3m";
        case BOLD       -> "\u001b[1m";
        case UNDERLINE  -> "\u001b[4m";
      });
    }
  }

  @Override
  protected void closeStyle(StringBuilder builder, TextStyle style, TextStyle last) {
    if (style == TextStyle.empty()) {
      builder.append("\u001b[0m"); // Reset all formatting
      if (last != TextStyle.empty()) {
        startStyle(builder, last);
      }
    }
  }
}
