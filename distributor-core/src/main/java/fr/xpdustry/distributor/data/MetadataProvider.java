package fr.xpdustry.distributor.data;

import fr.xpdustry.distributor.data.*;
import java.util.Optional;

public interface MetadataProvider {

  <T> Optional<T> getMetadata(final Key<T> key);
}
