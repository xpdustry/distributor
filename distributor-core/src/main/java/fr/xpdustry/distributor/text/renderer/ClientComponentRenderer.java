package fr.xpdustry.distributor.text.renderer;

final class ClientComponentRenderer extends AbstractComponentRenderer {

  static final ClientComponentRenderer INSTANCE = new ClientComponentRenderer();

  private ClientComponentRenderer() {
  }

  @Override
  protected void appendText(final StringBuilder builder, final String text) {
    builder.append(text.replace("[", "[["));
  }

  @Override
  protected void startStyle(final StringBuilder builder, final Style style) {
    if (style.getColor() != null) {
      builder.append("#").append(Integer.toHexString(style.getColor().getRGB()));
    }
  }

  @Override
  protected void closeStyle(final StringBuilder builder, final Style style, final Style last) {
    if (style.getColor() != null) {
      builder.append("[]");
    }
  }
}
