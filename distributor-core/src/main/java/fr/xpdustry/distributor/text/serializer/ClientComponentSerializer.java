package fr.xpdustry.distributor.text.serializer;

final class ClientComponentSerializer extends AbstractComponentSerializer {

  static final ClientComponentSerializer INSTANCE = new ClientComponentSerializer();

  private ClientComponentSerializer() {
  }

  @Override
  protected void appendText(Context context, String text) {
    super.appendText(context, text.replace("[", "[["));
  }

  @SuppressWarnings({"ConstantConditions", "NullAway"})
  @Override
  protected void startStyle(Context context) {
    if (context.isPreviousColorDifferentFromCurrent()) {
      context.appendText("[#" + Integer.toHexString(context.getCurrentColor().getRGB()) + "]");
    }
  }

  @Override
  protected void closeStyle(Context context) {
    if (context.isPreviousColorDifferentFromCurrent()) {
      context.appendText("[]");
    }
  }
}
