package fr.xpdustry.distributor.util;

import org.jetbrains.annotations.*;

public interface Buildable<T, B extends Buildable.Builder<T>> {

  @NotNull B toBuilder();

  interface Builder<T> {

    @NotNull T build();
  }
}
