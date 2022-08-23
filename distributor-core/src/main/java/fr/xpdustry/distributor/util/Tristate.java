package fr.xpdustry.distributor.util;

import org.checkerframework.checker.nullness.qual.Nullable;

public enum Tristate {
  FALSE,
  TRUE,
  UNDEFINED;

  public static Tristate of(final boolean state) {
    return state ? TRUE : FALSE;
  }

  public static Tristate of(final @Nullable Boolean state) {
    return state == null ? UNDEFINED : of(state.booleanValue());
  }
}
