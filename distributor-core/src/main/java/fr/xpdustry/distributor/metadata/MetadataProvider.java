package fr.xpdustry.distributor.metadata;

import io.leangen.geantyref.*;
import java.util.*;

public interface MetadataProvider {

  static MetadataProvider empty() {
    return EmptyMetadataProvider.INSTANCE;
  }

  <T> Optional<T> getMetadata(final String key, final TypeToken<T> type);

  default <T> Optional<T> getMetadata(final String key, final Class<T> type) {
    return getMetadata(key, TypeToken.get(type));
  }

  default <T> Optional<T> getMetadata(final Key<T> key) {
    return getMetadata(key.getName(), key.getValueType());
  }
}
