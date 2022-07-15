package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.format.TextStyle;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

final class ServerComponentRenderer extends AbstractComponentRenderer<Ansi> {

  static final ServerComponentRenderer INSTANCE = new ServerComponentRenderer();

  static {
    AnsiConsole.systemInstall();
  }

  private ServerComponentRenderer() {
  }

  @Override
  protected Ansi createBuilder() {
    return new Ansi();
  }

  @Override
  protected void appendText(Ansi builder, String text) {
    builder.a(text);
  }

  @Override
  protected void startStyle(Ansi builder, TextStyle style) {
    if (style.getColor() != null) {
      builder.fgRgb(style.getColor().getRGB());
    }
    for (final var decoration : style.getDecorations()) {
      builder.a(switch (decoration) {
        case ITALIC -> Ansi.Attribute.ITALIC;
        case BOLD -> Ansi.Attribute.INTENSITY_BOLD;
        case UNDERLINE -> Ansi.Attribute.UNDERLINE;
      });
    }
  }

  @Override
  protected void closeStyle(Ansi builder, TextStyle last) {
    builder.reset();
    if (last != TextStyle.empty()) {
      startStyle(builder, last);
    }
  }
}
