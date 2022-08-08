package fr.xpdustry.distributor.audience;

import arc.util.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.serializer.*;
import java.util.*;

final class ConsoleAudience implements Audience {

  static final ConsoleAudience INSTANCE = new ConsoleAudience();

  private ConsoleAudience() {
  }

  @Override
  public void sendMessage(final Component component) {
    // Printing on new log lines is more stylish lol...
    for (final var line : ComponentSerializer.server().serialize(component, this).split("\n", -1)) {
      Log.info(line);
    }
  }

  @Override
  public void sendWarning(final Component component) {
    for (final var line : ComponentSerializer.server().serialize(component, this).split("\n", -1)) {
      Log.warn(line);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMetadata(String key, Class<T> type) {
    if (key.equals(StandardMetaKeys.LOCALE.getNamespacedName())) {
      return (Optional<T>) Optional.of(Locale.getDefault());
    }
    return Optional.empty();
  }
}
