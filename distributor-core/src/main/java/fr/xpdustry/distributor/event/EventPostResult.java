package fr.xpdustry.distributor.event;

import java.util.*;
import org.jetbrains.annotations.*;

public final class EventPostResult {

  private static final EventPostResult SUCCESS = new EventPostResult(Collections.emptyMap());

  private final Map<Object, List<Throwable>> exceptions;

  public static @NotNull EventPostResult success() {
    return SUCCESS;
  }

  public static @NotNull EventPostResult failure(final @NotNull Map<Object, List<Throwable>> exceptions) {
    if (exceptions.isEmpty()) {
      throw new IllegalArgumentException("exceptions is empty");
    }
    return new EventPostResult(exceptions);
  }

  private EventPostResult(final @NotNull Map<Object, List<Throwable>> exceptions) {
    this.exceptions = Map.copyOf(exceptions);
  }

  public boolean isSuccess() {
    return this.exceptions.isEmpty();
  }

  public boolean isFailure() {
    return !isSuccess();
  }

  @Unmodifiable
  public @NotNull Map<Object, List<Throwable>> getExceptions() {
    return this.exceptions;
  }
}
