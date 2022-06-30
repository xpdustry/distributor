package fr.xpdustry.distributor.command.sender;

import arc.util.*;
import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

/**
 * A command sender representing the server.
 */
public final class ArcServerSender implements ArcCommandSender {

  @Override
  public boolean isPlayer() {
    return false;
  }

  @Override
  public @NotNull Player getPlayer() {
    throw new UnsupportedOperationException("Cannot convert console to player");
  }

  /**
   * Returns the {@link Locale#getDefault() default locale} of the system.
   */
  @Override
  public @NotNull Locale getLocale() {
    return Locale.getDefault();
  }

  @Override
  public @NotNull Collection<String> getPermissions() {
    return Collections.emptyList();
  }
}
