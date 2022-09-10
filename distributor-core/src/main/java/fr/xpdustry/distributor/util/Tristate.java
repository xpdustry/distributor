package fr.xpdustry.distributor.util;

import org.jetbrains.annotations.*;

public enum Tristate {
  FALSE,
  TRUE,
  UNDEFINED;

  public static @NotNull Tristate of(final boolean state) {
    return state ? TRUE : FALSE;
  }

  public static @NotNull Tristate of(final @Nullable Boolean state) {
    return state == null ? UNDEFINED : of(state.booleanValue());
  }
}
