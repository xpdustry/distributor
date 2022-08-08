package fr.xpdustry.distributor.util;

public interface Buildable<T, B extends Buildable.Builder<T>> {

  B toBuilder();

  interface Builder<T> {

    T build();
  }
}
