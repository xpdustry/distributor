package fr.xpdustry.distributor.exception;

import org.jetbrains.annotations.NotNull;

/**
 * This error is thrown when a script is running for too long.
 */
public class BlockingScriptError extends Error {

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
