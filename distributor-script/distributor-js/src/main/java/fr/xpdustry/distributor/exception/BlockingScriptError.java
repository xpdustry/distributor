package fr.xpdustry.distributor.exception;

import java.io.Serial;
import org.jetbrains.annotations.NotNull;

/**
 * This error is thrown when a script is running for too long.
 */
public final class BlockingScriptError extends Error {

  @Serial
  private static final long serialVersionUID = 7267751075275052115L;

  private final int maxRuntime;

  public BlockingScriptError(final int maxRuntime) {
    super();
    this.maxRuntime = maxRuntime;
  }

  @Override
  public @NotNull String getMessage() {
    return "The script as ran for more than " + maxRuntime + " seconds.";
  }

  public int getMaxRuntime() {
    return maxRuntime;
  }
}
