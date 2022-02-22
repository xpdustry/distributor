package fr.xpdustry.distributor.command.sender;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A command sender representing a player.
 */
public class ArcClientSender implements ArcCommandSender {

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
  public boolean hasPermission(@NotNull String permission) {
    return permissions.contains(permission);
  }

  @Override
  public void addPermission(@NotNull String permission) {
    permissions.add(permission);
  }

  @Override
  public void removePermission(@NotNull String permission) {
    permissions.remove(permission);
  }

  @Override
  public @NotNull Collection<String> getPermissions() {
    return Collections.unmodifiableCollection(permissions);
  }

  @Override
  public void sendMessage(@NotNull String message) {
    player.sendMessage(message);
  }
}
