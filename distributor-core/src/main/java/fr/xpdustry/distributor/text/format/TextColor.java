package fr.xpdustry.distributor.text.format;

// TODO Add colors
public final class TextColor {

  private static final int WHITE_VALUE = 0xFFFFFF;
  private static final int BLACK_VALUE = 0x000000;
  private static final int RED_VALUE = 0xFF0000;
  private static final int YELLOW_VALUE = 0xFFFF00;
  private static final int ACCENT_VALUE = 0xFFD37F;

  public static final TextColor WHITE = new TextColor(WHITE_VALUE);
  public static final TextColor BLACK = new TextColor(BLACK_VALUE);
  public static final TextColor RED = new TextColor(RED_VALUE);
  public static final TextColor YELLOW = new TextColor(YELLOW_VALUE);
  public static final TextColor ACCENT = new TextColor(ACCENT_VALUE);

  private final int value;

  private TextColor(final int value) {
    this.value = value;
  }

  public static TextColor of(arc.graphics.Color color) {
    return ofRGB(color.rgb888());
  }

  public static TextColor ofRGB(final int value) {
    return switch (value & 0xFFFFFF) {
      case WHITE_VALUE -> TextColor.WHITE;
      case BLACK_VALUE -> TextColor.BLACK;
      case RED_VALUE -> TextColor.RED;
      case ACCENT_VALUE -> TextColor.ACCENT;
      default -> new TextColor(value);
    };
  }

  public static TextColor ofRGB(final int r, final int g, final int b) {
    return ofRGB((r & 0xFF << 16) | (g & 0xFF << 8) | (b & 0xFF));
  }

  public int getRGB() {
    return value;
  }

  public int getR() {
    return value >> 16 & 0xFF;
  }

  public int getG() {
    return value >> 8 & 0xFF;
  }

  public int getB() {
    return value & 0xFF;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || (obj instanceof TextColor color && this.value == color.value);
  }
}
