package fr.xpdustry.distributor.command;

import fr.xpdustry.distributor.message.*;
import fr.xpdustry.distributor.permission.*;
import org.jetbrains.annotations.*;

/**
 * This class represents the command sender, it can be either the console or a player.
 */
public interface CommandSender extends MessageReceiver, Permissible {

  @NotNull String getName();

  boolean isPlayer();
}
