package fr.xpdustry.distributor.legacy.command.sender;

import fr.xpdustry.distributor.legacy.message.*;
import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

/**
 * This class represents the command sender, it can be either the console or a player.
 */
public interface ArcCommandSender extends MessageReceiver {

  boolean isPlayer();

  /**
   * Returns the internal {@link Player} instance of this command sender.
   * This method may only be called safely if {@link #isPlayer()} returns true.
   *
   * @return the player representation of the sender
   * @throws UnsupportedOperationException if the sender does not support this operation
   */
  @NotNull Player getPlayer();

  @NotNull Locale getLocale();

  /**
   * Checks if the sender has a permission.
   *
   * @param permission the permission
   * @return whether the sender has the permission or not, or return true if the permission is blank
   * @see cloud.commandframework.CommandManager#hasPermission(Object, String)
   */
  boolean hasPermission(final @NotNull String permission);

  void addPermission(final @NotNull String permission);

  void removePermission(final @NotNull String permission);

  /**
   * Returns an unmodifiable view of the permissions of this command sender.
   */
  @NotNull Collection<String> getPermissions();
}
