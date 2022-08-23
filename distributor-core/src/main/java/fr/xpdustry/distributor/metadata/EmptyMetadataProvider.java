package fr.xpdustry.distributor.metadata;

import io.leangen.geantyref.*;
import java.util.*;

final class EmptyMetadataProvider implements MetadataProvider {

  static final EmptyMetadataProvider INSTANCE = new EmptyMetadataProvider();

  private EmptyMetadataProvider() {
  }

  @Override
  public <T> Optional<T> getMetadata(final String key, final TypeToken<T> type) {
    return Optional.empty();
  }
}
