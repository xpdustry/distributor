package fr.xpdustry.distributor.command.sender;

import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

/**
 * This class represents the command sender, it can be either the console or a player.
 */
public interface ArcCommandSender {

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
   * Returns an unmodifiable view of the permissions of this command sender.
   */
  @NotNull Collection<String> getPermissions();
}
