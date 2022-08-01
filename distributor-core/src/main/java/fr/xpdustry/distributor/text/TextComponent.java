package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.format.*;

public final class TextComponent extends Component {

  static final TextComponent EMPTY = Components.text("");
  static final TextComponent NEWLINE = Components.text("\n");
  static final TextComponent SPACE = Components.text(" ");

  private final String content;

  TextComponent(final String content, final TextStyle style) {
    super(style);
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  @Override
  public Component repeat(int times) {
    if (times <= 0 || isEmpty()) {
      return Components.empty();
    }
    return new TextComponent(content.repeat(times), getStyle());
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
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
    public Builder from(TextComponent component) {
      this.content(component.content);
      return super.from(component);
    }

    @Override
    public TextComponent build() {
      return content.isEmpty() ? EMPTY : new TextComponent(content, style());
    }
  }
}
