package fr.xpdustry.distributor.audience;

import arc.util.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.renderer.*;
import java.util.*;

final class ConsoleAudience implements Audience {

  static final ConsoleAudience INSTANCE = new ConsoleAudience();

  private ConsoleAudience() {
  }

  @Override
  public void sendMessage(final Component component) {
    Log.info(ComponentRenderer.server().render(component));
  }

  @Override
  public void sendWarning(final Component component) {
    Log.warn(ComponentRenderer.server().render(component));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMetadata(final Key<T> key) {
    if (key.equals(StandardMetaKeys.LOCALE)) {
      return (Optional<T>) Optional.of(Locale.getDefault());
    } else if (key.equals(StandardMetaKeys.SERVER)) {
      return (Optional<T>) Optional.of(true);
    }
    return Optional.empty();
  }
}
