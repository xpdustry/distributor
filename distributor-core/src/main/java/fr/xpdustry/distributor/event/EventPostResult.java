package fr.xpdustry.distributor.event;

import java.util.*;
import org.jetbrains.annotations.*;

public final class EventPostResult {

  private static final EventPostResult SUCCESS = new EventPostResult(Collections.emptyMap());

  private final Map<EventSubscriber<?>, Throwable> exceptions;

  public static @NotNull EventPostResult success() {
    return SUCCESS;
  }

  public static @NotNull EventPostResult failure(final @NotNull Map<EventSubscriber<?>, Throwable> exceptions) {
    if (exceptions.isEmpty()) {
      throw new IllegalArgumentException("exceptions is empty");
    }
    return new EventPostResult(exceptions);
  }

  private EventPostResult(final @NotNull Map<EventSubscriber<?>, Throwable> exceptions) {
    this.exceptions = Map.copyOf(exceptions);
  }

  public boolean isSuccess() {
    return this.exceptions.isEmpty();
  }

  public boolean isFailure() {
    return !isSuccess();
  }

  @Unmodifiable
  public @NotNull Map<EventSubscriber<?>, Throwable> getExceptions() {
    return this.exceptions;
  }
}
