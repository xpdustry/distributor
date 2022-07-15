package fr.xpdustry.distributor.meta;

import java.util.Optional;

public interface MetaProvider {

  <T> Optional<T> getMeta(final MetaKey<T> key);
}
