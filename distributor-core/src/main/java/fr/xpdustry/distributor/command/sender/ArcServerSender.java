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

  /**
   * Since it's the console, it always returns true.
   */
  @Override
  public boolean hasPermission(final @NotNull String permission) {
    return true;
  }

  @Override
  public void addPermission(@NotNull String permission) {
  }

  @Override
  public void removePermission(@NotNull String permission) {
  }

  @Override
  public @NotNull Collection<String> getPermissions() {
    return Collections.emptyList();
  }

  @Override
  public void sendMessage(@NotNull String message) {
    Log.info(message);
  }
}
