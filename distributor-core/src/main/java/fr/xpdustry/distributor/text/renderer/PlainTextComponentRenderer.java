package fr.xpdustry.distributor.text.renderer;

final class PlainTextComponentRenderer extends AbstractComponentRenderer {

  static final PlainTextComponentRenderer INSTANCE = new PlainTextComponentRenderer();

  private PlainTextComponentRenderer() {
  }

  @Override
  protected void startStyle(StringBuilder builder, Style style) {
  }

  @Override
  protected void closeStyle(StringBuilder builder, Style style, Style last) {
  }
}
