package fr.xpdustry.distributor.command.sender;

import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

/**
 * A command sender representing a player.
 */
public final class ArcClientSender implements ArcCommandSender {

  private final @NotNull Player player;
  private final Collection<String> permissions = new HashSet<>();

  public ArcClientSender(final @NotNull Player player) {
    this.player = player;
  }

  @Override
  public boolean isPlayer() {
    return true;
  }

  @Override
  public @NotNull Player getPlayer() {
    return player;
  }

  @Override
  public @NotNull Locale getLocale() {
    return Locale.forLanguageTag(player.locale().replace('_', '-'));
  }

  @Override
  public @NotNull Collection<String> getPermissions() {
    return Collections.unmodifiableCollection(permissions);
  }
}
