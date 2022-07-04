package fr.xpdustry.distributor.permission;

import fr.xpdustry.distributor.admin.*;
import java.util.*;
import org.jetbrains.annotations.*;

public interface Permissible extends ServerAdministrator {

  default boolean isPermissionSet(final @NotNull String permission) {
    final var lower = permission.toLowerCase(Locale.ROOT);
    return getAttachements().stream()
      .map(a -> a.getPermission(this, lower))
      .allMatch(p -> p == PermissionValue.UNSET);
  }

  default boolean hasPermission(final @NotNull String permission) {
    final var lower = permission.toLowerCase(Locale.ROOT);
    return getAttachements().stream()
      .map(a -> a.getPermission(this, lower))
      .anyMatch(p -> p == PermissionValue.TRUE);
  }

  default @NotNull Collection<String> getPermissions() {
    return getAttachements().stream()
      .flatMap(a -> a.getPermissions(this).stream())
      .toList();
  }

  void addAttachement(final @NotNull PermissionAttachement attachement);

  void removeAttachement(final @NotNull PermissionAttachement attachement);

  @NotNull Collection<PermissionAttachement> getAttachements();
}
