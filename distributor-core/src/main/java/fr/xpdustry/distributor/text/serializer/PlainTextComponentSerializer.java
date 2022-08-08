package fr.xpdustry.distributor.text.serializer;

final class PlainTextComponentSerializer extends AbstractComponentSerializer {

  static final PlainTextComponentSerializer INSTANCE = new PlainTextComponentSerializer();

  private PlainTextComponentSerializer() {
  }

  @Override
  protected void startStyle(Context context) {
  }

  @Override
  protected void closeStyle(Context context) {
  }
}
