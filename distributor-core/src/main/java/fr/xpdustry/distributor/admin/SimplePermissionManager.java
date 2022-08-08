package fr.xpdustry.distributor.admin;

import fr.xpdustry.distributor.io.*;
import fr.xpdustry.distributor.struct.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import mindustry.*;
import org.spongepowered.configurate.loader.*;
import org.spongepowered.configurate.yaml.*;

public final class SimplePermissionManager implements PermissionManager, FileResource {

  private final Path path;
  private final ConfigurationLoader<?> loader;

  private final Set<String> defaultPermissions = new HashSet<>();
  private final Map<String, Set<String>> playerPermissions = new HashMap<>();

  private final Object lock = new Object();

  public SimplePermissionManager(final Path directory, final String filename) {
    this.path = directory.resolve(filename + ".yaml");
    this.loader = YamlConfigurationLoader.builder().path(path).build();
  }

  @Override
  public boolean hasPermission(final MUUID muuid, final String permission) {
    synchronized (lock) {
      if (Vars.netServer.admins.isAdmin(muuid.getUUID(), muuid.getUSID()) || defaultPermissions.contains(permission)) {
        return true;
      }
      final var original = MUUID.of(Vars.netServer.admins.getInfo(muuid.getUUID()));
      return original.equals(muuid) && playerPermissions.getOrDefault(muuid.getUUID(), Collections.emptySet()).contains(permission);
    }
  }

  @Override
  public void load() throws IOException {
    final var root = loader.load();
    synchronized (lock) {
      defaultPermissions.clear();
      defaultPermissions.addAll(root.node("defaults").getList(String.class, Collections.emptyList()));
      playerPermissions.clear();
      for (final var entry : root.node("players").childrenMap().entrySet()) {
        final var permissions = Set.copyOf(entry.getValue().getList(String.class, Collections.emptyList()));
        playerPermissions.put((String) entry.getKey(), permissions);
      }
    }
  }

  @Override
  public void save() throws IOException {
    throw new IOException("Can't save this configuration manager.");
  }

  @Override
  public Path getPath() {
    return path;
  }
}
