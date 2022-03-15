package fr.xpdustry.distributor.exception;

import java.io.Serial;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a wrapper for rhino exceptions.
 */
public final class ScriptException extends Exception {

  @Serial
  private static final long serialVersionUID = -7447784899077850652L;

  public ScriptException(final @NotNull Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    return getCause().getClass().getSimpleName() + (getCause().getMessage() == null ? "" : ": " + getCause().getMessage());
  }
}
