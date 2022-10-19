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
package fr.xpdustry.distributor.core.permission;

import fr.xpdustry.distributor.api.permission.*;
import fr.xpdustry.distributor.api.util.*;
import java.nio.file.*;
import java.util.*;
import mindustry.*;
import org.slf4j.*;
import org.slf4j.Logger;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.yaml.*;

public final class SimplePermissionService implements PermissionService {

  private static final Logger logger = LoggerFactory.getLogger(SimplePermissionService.class);
  private static final Comparator<GroupPermissible> GROUP_COMPARATOR =
    Comparator.comparing(GroupPermissible::getWeight).reversed();

  private final PlayerPermissibleManager players;
  private final GroupPermissibleManager groups;
  private final YamlConfigurationLoader loader;

  private String primaryGroup;
  private boolean verifyAdmin;

  public SimplePermissionService(final Path directory) {
    this.loader = YamlConfigurationLoader.builder()
      .indent(2)
      .path(directory.resolve("settings.yaml"))
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
  public Tristate getPermission(String uuid, String permission) {
    logger.trace("Lookup permission {} for {}.", permission, uuid);
    if (verifyAdmin) {
      final var info = Vars.netServer.admins.getInfoOptional(uuid);
      if (info != null && info.admin) {
        return Tristate.TRUE;
      }
    }

    final var perm = permission.toLowerCase(Locale.ROOT);
    var state = Tristate.UNDEFINED;
    final var visited = new HashSet<String>();
    final Queue<Permissible> queue = new ArrayDeque<>();
    final var player = players.findById(uuid);
    final var primary = groups.findById(primaryGroup);

    if (player.isPresent()) {
      queue.add(player.get());
    } else if (primary.isPresent()) {
      queue.add(primary.get());
    } else {
      return state;
    }

    while (!queue.isEmpty()) {
      final var holder = queue.remove();
      state = holder.getPermission(perm);
      if (state != Tristate.UNDEFINED) {
        break;
      }

      holder.getParentGroups()
        .stream()
        .filter(visited::add)
        .map(groups::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted(GROUP_COMPARATOR)
        .forEach(queue::add);

      if (queue.isEmpty() && !visited.add(primaryGroup) && primary.isPresent()) {
        queue.add(primary.get());
      }
    }

    return state;
  }

  @Override
  public String getPrimaryGroup() {
    return primaryGroup;
  }

  @Override
  public void setPrimaryGroup(String group) {
    this.primaryGroup = group;
    save();
  }

  @Override
  public boolean getVerifyAdmin() {
    return verifyAdmin;
  }

  @Override
  public void setVerifyAdmin(boolean status) {
    this.verifyAdmin = status;
    save();
  }

  @Override
  public PlayerPermissibleManager getPlayerPermissionManager() {
    return players;
  }

  @Override
  public GroupPermissibleManager getGroupPermissionManager() {
    return groups;
  }

  private void save() {
    try {
      final var root = loader.createNode();
      root.node("primary-group").set(primaryGroup);
      root.node("verify-admin").set(verifyAdmin);
      loader.save(root);
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Failed to save the permission manager settings.", e);
    }
  }
}
