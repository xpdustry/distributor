package fr.xpdustry.distributor.text.serializer;

import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;

public interface ComponentSerializer<O> {

  static ComponentSerializer<String> server() {
    return ServerComponentSerializer.INSTANCE;
  }

  static ComponentSerializer<String> client() {
    return ClientComponentSerializer.INSTANCE;
  }

  static ComponentSerializer<String> text() {
    return PlainTextComponentSerializer.INSTANCE;
  }

  O serialize(final Component component, final MetadataContainer metadata);
}
