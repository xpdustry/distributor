package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.format.TextStyle;

final class ClientComponentRenderer extends AbstractComponentRenderer<StringBuilder> {

  static final ClientComponentRenderer INSTANCE = new ClientComponentRenderer();

  private ClientComponentRenderer() {
  }

  @Override
  protected StringBuilder createBuilder() {
    return new StringBuilder();
  }

  @Override
  protected void appendText(StringBuilder builder, String text) {
    builder.append(text.replace("[", "[["));
  }

  @Override
  protected void startStyle(StringBuilder builder, TextStyle style) {
    if (style.getColor() != null) {
      builder.append("#").append(Integer.toHexString(style.getColor().getRGB()));
    }
  }

  @Override
  protected void closeStyle(StringBuilder builder, TextStyle last) {
    if (last != TextStyle.empty()) {
      builder.append("[]");
    }
  }
}
