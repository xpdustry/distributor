package fr.xpdustry.distributor.text.renderer;

final class ServerComponentRenderer extends AbstractComponentRenderer {

  static final ServerComponentRenderer INSTANCE = new ServerComponentRenderer();

  private static final String RESET = "\u001b[0m";
  private static final String CONSOLE_COLOR = "\u001b[38;2;%d;%d;%dm";

  private ServerComponentRenderer() {
  }

  @Override
  protected void startStyle(final StringBuilder builder, final Style style) {
    final var color = style.getColor();
    if (color != null) {
      builder.append(CONSOLE_COLOR.formatted(color.getR(), color.getG(), color.getB()));
    }
    for (final var decoration : style.getDecorations()) {
      builder.append(
        switch (decoration) {
          case ITALIC -> "\u001b[3m";
          case BOLD -> "\u001b[1m";
          case UNDERLINE -> "\u001b[4m";
        }
      );
    }
  }

  @Override
  protected void closeStyle(final StringBuilder builder, final Style style, final Style last) {
    if (style.getColor() != null || !style.getDecorations().isEmpty()) {
      builder.append(RESET);
      startStyle(builder, last);
    }
  }
}
