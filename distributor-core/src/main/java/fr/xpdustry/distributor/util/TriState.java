package fr.xpdustry.distributor.util;

import org.checkerframework.checker.nullness.qual.Nullable;

// TODO Hehe
public enum TriState {
  FALSE,
  TRUE,
  UNDEFINED;

  public static TriState of(boolean state) {
    return state ? TRUE : FALSE;
  }

  public static TriState of(@Nullable Boolean state) {
    return state == null ? UNDEFINED : of(state.booleanValue());
  }
}
