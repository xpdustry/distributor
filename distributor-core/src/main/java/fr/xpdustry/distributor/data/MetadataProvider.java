package fr.xpdustry.distributor.data;

import java.util.*;

public interface MetadataProvider {

  <T> Optional<T> getMetadata(final Key<T> key);
}
