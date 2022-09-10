package fr.xpdustry.distributor.audience;

import arc.util.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.serializer.*;
import java.util.*;

final class ConsoleAudience implements Audience {

  static final ConsoleAudience INSTANCE = new ConsoleAudience();

  private final MetadataContainer metadata = MetadataContainer.builder()
    .withConstant(StandardKeys.PRIVILEGED, true)
    .withSupplier(StandardKeys.LOCALE, Locale::getDefault)
    .build();

  private ConsoleAudience() {
  }

  @Override
  public void sendMessage(final Component component) {
    for (final var line : ComponentSerializer.server().serialize(component, this.metadata).split("\n", -1)) {
      Log.info(line);
    }
  }

  @Override
  public void sendWarning(final Component component) {
    for (final var line : ComponentSerializer.server().serialize(component, this.metadata).split("\n", -1)) {
      Log.warn(line);
    }
  }

  @Override
  public MetadataContainer getMetadata() {
    return metadata;
  }
}
