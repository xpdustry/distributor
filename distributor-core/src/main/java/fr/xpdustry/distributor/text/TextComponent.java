package fr.xpdustry.distributor.text;

import fr.xpdustry.distributor.text.TextComponent.*;
import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.util.*;

public final class TextComponent extends Component implements Buildable<TextComponent, Builder> {

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
      return EMPTY;
    }
    return new TextComponent(content.repeat(times), getStyle());
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
  }

  @Override
  public Builder toBuilder() {
    return new Builder().withStyle(getStyle()).withContent(content);
  }

  public static final class Builder extends ComponentBuilder<TextComponent, Builder> {

    private String content = "";

    Builder() {
    }

    public Builder withContent(final String content) {
      this.content = content;
      return this;
    }

    public String getContent() {
      return this.content;
    }

    @Override
    public TextComponent build() {
      return content.isEmpty() ? EMPTY : new TextComponent(content, getStyle());
    }
  }

  @Override
  public String toString() {
    return "{content=" + content + "}";
  }
}
