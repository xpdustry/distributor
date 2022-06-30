package fr.xpdustry.distributor.admin;

import java.util.*;
import org.jetbrains.annotations.*;

public interface Permissible {

  boolean hasPermission(final @NotNull String permission);

  void addPermission(final @NotNull String permission);

  void removePermission(final @NotNull String permission);

  @NotNull Set<String> getPermissions();
}
