package fr.xpdustry.distributor.command.sender;

import fr.xpdustry.distributor.string.MessageFormatter;
import fr.xpdustry.distributor.string.MessageReceiver;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents the command sender, it can be either the console or a player.
 */
public abstract class ArcCommandSender implements MessageReceiver {

  private final MessageFormatter formatter;
  private final Collection<String> permissions = new HashSet<>();

  public ArcCommandSender(final @NotNull MessageFormatter formatter) {
    this.formatter = formatter;
  }

  public ArcCommandSender() {
    this(MessageFormatter.simple());
  }

  public abstract boolean isPlayer();

  /**
   * Returns the internal {@link Player} instance of this command sender. This method may only be called safely if
   * {@link #isPlayer()} returns true.
   *
   * @return the player representation of the sender
   * @throws UnsupportedOperationException if the sender does not support this operation
   */
  public abstract @NotNull Player asPlayer();

  public abstract @NotNull Locale getLocale();

  /**
   * Checks if the sender has a permission.
   *
   * @param permission the permission
   * @return whether the sender has the permission or not, or return true if the permission is blank
   * @see cloud.commandframework.CommandManager#hasPermission(Object, String)
   */
  public boolean hasPermission(final @NotNull String permission) {
    return permission.isBlank() || permissions.contains(permission);
  }

  public void addPermission(final @NotNull String permission) {
    permissions.add(permission);
  }

  public void removePermission(final @NotNull String permission) {
    permissions.remove(permission);
  }

  public @NotNull MessageFormatter getFormatter() {
    return formatter;
  }

  /** Returns an unmodifiable view of the permissions of this command sender. */
  public @NotNull Collection<String> getPermissions() {
    return Collections.unmodifiableCollection(permissions);
  }
}
