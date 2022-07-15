package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.TextStyle;

public final class TextComponent extends Component {

  static final TextComponent EMPTY = text("");
  static final TextComponent NEWLINE = text("\n");
  static final TextComponent SPACE = text(" ");

  private final String content;

  TextComponent(final String content, final TextStyle style) {
    super(style);
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public static final class Builder extends Component.Builder<TextComponent, Builder> {

    private String content = "";

    public Builder content(final String content) {
      this.content = content;
      return this;
    }

    public String content() {
      return this.content;
    }

    @Override
    public TextComponent build() {
      return content.isEmpty() ? EMPTY : new TextComponent(content, style());
    }
  }
}
