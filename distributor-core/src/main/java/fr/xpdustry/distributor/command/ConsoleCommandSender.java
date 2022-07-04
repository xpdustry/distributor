package fr.xpdustry.distributor.command;

import arc.util.*;
import fr.xpdustry.distributor.permission.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * A command sender representing the server.
 */
public final class ConsoleCommandSender implements CommandSender {

  private final Set<PermissionAttachement> attachements = new HashSet<>();

  @Override
  public boolean isAdministrator() {
    return true;
  }

  @Override
  public void setAdministrator(final boolean administrator) {
    throw new UnsupportedOperationException("This sender is the console...");
  }

  @Override
  public @NotNull String getName() {
    return "Server";
  }

  @Override
  public void sendMessage(final @NotNull String message) {
    Log.info(message);
  }

  @Override
  public boolean isPlayer() {
    return false;
  }

  @Override
  public void addAttachement(final @NotNull PermissionAttachement attachement) {
    attachements.add(attachement);
  }

  @Override
  public void removeAttachement(final @NotNull PermissionAttachement attachement) {
    attachements.remove(attachement);
  }

  @Override
  public @NotNull Collection<PermissionAttachement> getAttachements() {
    return Collections.unmodifiableCollection(attachements);
  }
}
