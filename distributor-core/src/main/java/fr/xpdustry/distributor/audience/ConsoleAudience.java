package fr.xpdustry.distributor.audience;

import arc.util.Log;
import fr.xpdustry.distributor.meta.MetaKey;
import fr.xpdustry.distributor.text.Component;
import fr.xpdustry.distributor.text.renderer.ComponentRenderer;
import java.util.Locale;
import java.util.Optional;

final class ConsoleAudience implements Audience {

  static final ConsoleAudience INSTANCE = new ConsoleAudience();

  private ConsoleAudience() {
  }

  @Override
  public void sendMessage(final Component component) {
    Log.info(ComponentRenderer.server().render(component));
  }

  @Override
  public void sendWarning(Component component) {
    Log.warn(ComponentRenderer.server().render(component));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMeta(final MetaKey<T> key) {
    if (key.equals(MetaKey.LOCALE)) {
      return (Optional<T>) Optional.of(Locale.getDefault());
    }
    return Optional.empty();
  }
}
