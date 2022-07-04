package fr.xpdustry.distributor.permission;

import java.util.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

public interface PermissionAttachement {

  @Nullable Plugin getPlugin();

  @NotNull PermissionValue getPermission(final @NotNull Permissible permissible, final @NotNull String permission);

  @NotNull Collection<String> getPermissions(final @NotNull Permissible permissible);
}
