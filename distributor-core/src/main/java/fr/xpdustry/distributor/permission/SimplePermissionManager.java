/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.permission;

import fr.xpdustry.distributor.util.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import mindustry.*;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.yaml.*;

public class SimplePermissionManager implements PermissionManager {

  private static final Comparator<GroupPermissible> GROUP_COMPARATOR =
    Comparator.comparing(GroupPermissible::getWeight);

  private final PlayerPermissibleManager players;
  private final GroupPermissibleManager groups;
  private final YamlConfigurationLoader loader;

  private String primaryGroup;
  private boolean verifyAdmin;

  public SimplePermissionManager(final Path directory) {
    this.loader = YamlConfigurationLoader.builder()
      .indent(2)
      .path(directory.resolve("preferences.yaml"))
      .nodeStyle(NodeStyle.BLOCK)
      .build();
    this.players = new SimplePlayerPermissibleManager(directory.resolve("players.yaml"));
    this.groups = new SimpleGroupPermissibleManager(directory.resolve("groups.yaml"));

    try {
      final var root = loader.load();
      this.primaryGroup = root.node("primary-group").getString("default");
      this.verifyAdmin = root.node("verify-admin").getBoolean(true);
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Unable to load the permissions.", e);
    }
  }

  @Override
  public CompletableFuture<Boolean> hasPermission(String uuid, String permission) {
    if (verifyAdmin && Vars.netServer.admins.getInfoOptional(uuid).admin) {
      return CompletableFuture.completedFuture(true);
    }
    return CompletableFuture.supplyAsync(() -> {
      final var perm = permission.toLowerCase(Locale.ROOT);
      var state = Tristate.UNDEFINED;
      final var visited = new HashSet<String>();
      final var queue = new ArrayDeque<Permissible>();

      final var player = players.findById(uuid).join();
      final var primary = groups.findById(primaryGroup).join();
      if (player.isPresent()) {
        queue.add(player.get());
      } else if (primary.isPresent()) {
        queue.add(primary.get());
      } else {
        return false;
      }

      while (!queue.isEmpty()) {
        final var holder = queue.remove();
        state = holder.getPermission(perm);
        if (state != Tristate.UNDEFINED) {
          break;
        }
        holder.getParentGroups()
          .stream()
          .filter(g -> visited.add(g) && groups.existsById(g).join())
          .map(g -> groups.findById(g).join())
          .filter(Optional::isPresent)
          .map(Optional::get)
          .sorted(GROUP_COMPARATOR)
          .forEach(queue::add);
        if (queue.isEmpty() && !visited.add(primaryGroup) && primary.isPresent()) {
          queue.add(primary.get());
        }
      }
      return state.asBoolean();
    });
  }

  @Override
  public PlayerPermissibleManager getPlayerPermissionManager() {
    return players;
  }

  @Override
  public GroupPermissibleManager getGroupPermissionManager() {
    return groups;
  }

  @Override
  public CompletableFuture<String> getPrimaryGroup() {
    return CompletableFuture.completedFuture(primaryGroup);
  }

  @Override
  public CompletableFuture<Void> setPrimaryGroup(String group) {
    this.primaryGroup = group;
    return save();
  }

  @Override
  public CompletableFuture<Boolean> getVerifyAdmin() {
    return CompletableFuture.completedFuture(verifyAdmin);
  }

  @Override
  public CompletableFuture<Void> setVerifyAdmin(boolean status) {
    this.verifyAdmin = status;
    return save();
  }

  private CompletableFuture<Void> save() {
    try {
      final var root = loader.createNode();
      root.node("primary-group").set(primaryGroup);
      root.node("verify-admin").set(verifyAdmin);
      loader.save(root);
      return CompletableFuture.completedFuture(null);
    } catch (final ConfigurateException e) {
      return CompletableFuture.failedFuture(e);
    }
  }
}
