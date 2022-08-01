package fr.xpdustry.distributor.text.renderer;

import fr.xpdustry.distributor.text.*;

public interface ComponentRenderer {

  static ComponentRenderer server() {
    return ServerComponentRenderer.INSTANCE;
  }

  static ComponentRenderer client() {
    return ClientComponentRenderer.INSTANCE;
  }

  String render(final Component component);
}
