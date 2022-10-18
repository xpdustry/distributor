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

import fr.xpdustry.distributor.persistence.*;
import fr.xpdustry.distributor.util.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.yaml.*;

abstract class AbstractPermissibleManager<T extends Permissible> implements PersistenceManager<T, String> {

  private final Map<String, T> permissibles = new HashMap<>();
  private final YamlConfigurationLoader loader;

  protected AbstractPermissibleManager(final Path path) {
    /* TODO The check does not work, fix this...
    if (!path.endsWith(".yml") || !path.endsWith(".yaml")) {
      throw new IllegalArgumentException("Unsupported file extension " + path);
    }
     */

    this.loader = YamlConfigurationLoader.builder()
      .indent(2)
      .path(path)
      .nodeStyle(NodeStyle.BLOCK)
      .build();

    try {
      final var root = this.loader.load();
      for (final var entry : root.childrenMap().entrySet()) {
        final var permissible = createPermissible((String) entry.getKey());
        loadPermissibleData(permissible, entry.getValue());
        permissibles.put(getIdentifier(permissible), permissible);
      }
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Unable to load the permissions.", e);
    }
  }

  @Override
  public CompletableFuture<T> findOrCreateById(String id) {
    return CompletableFuture.completedFuture(permissibles.containsKey(id) ? permissibles.get(id) : createPermissible(id));
  }

  @Override
  public CompletableFuture<Optional<T>> findById(String id) {
    return CompletableFuture.completedFuture(Optional.ofNullable(permissibles.get(id)));
  }

  @Override
  public CompletableFuture<List<T>> findAll() {
    return CompletableFuture.completedFuture(List.copyOf(permissibles.values()));
  }

  @Override
  public CompletableFuture<Long> count() {
    return CompletableFuture.completedFuture((long) permissibles.size());
  }

  @Override
  public CompletableFuture<Void> save(T entity) {
    permissibles.put(getIdentifier(entity), entity);
    return save();
  }

  @Override
  public CompletableFuture<Void> deleteById(String id) {
    return permissibles.remove(id) != null
      ? save()
      : CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Void> delete(T entity) {
    return deleteById(getIdentifier(entity));
  }

  @Override
  public CompletableFuture<Void> deleteAll() {
    permissibles.clear();
    return save();
  }

  protected void loadPermissibleData(final T permissible, final ConfigurationNode node) throws ConfigurateException {
    for (final var parent : node.node("parents").getList(String.class, Collections.emptyList())) {
      permissible.addParent(parent);
    }
    for (final var entry : node.node("permissions").childrenMap().entrySet()) {
      permissible.setPermission((String) entry.getKey(), Tristate.of(entry.getValue().getBoolean()));
    }
  }

  protected void savePermissibleData(final T permissible, final ConfigurationNode node) throws ConfigurateException {
    for (final var parent : permissible.getParentGroups()) {
      node.node("parents").appendListNode().set(parent);
    }
    for (final var permission : permissible.getPermissions().entrySet()) {
      node.node("permissions", permission.getKey()).set(permission.getValue());
    }
  }

  protected abstract T createPermissible(final String id);

  protected abstract String getIdentifier(final T permissible);

  private CompletableFuture<Void> save() {
    try {
      final var root = loader.createNode();
      for (final var permissible : permissibles.values()) {
        final var node = root.node(getIdentifier(permissible));
        savePermissibleData(permissible, node);
      }
      loader.save(root);
      return CompletableFuture.completedFuture(null);
    } catch (final ConfigurateException e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  /*
  private final Map<String, PlayerPermissible> players = new HashMap<>();
  private final Map<String, GroupPermissible> groups = new HashMap<>();
  private final ConfigurationLoader<?> loader;
  private String primaryGroup;
  private boolean verifyAdmin;

  public SimplePermissionManager(final Path path) {
    if (path.endsWith(".yml")) {
      throw new IllegalArgumentException("Unsupported file extension " + path);
    }

    this.loader = YamlConfigurationLoader.builder()
      .indent(2)
      .path(path)
      .nodeStyle(NodeStyle.BLOCK)
      .build();

    try {
      final var root = loader.load();
      this.primaryGroup = root.node("primary-group").getString("default");
      this.verifyAdmin = root.node("verify-admin").getBoolean(true);
      for (final var entry : root.node("players").childrenMap().entrySet()) {
        final var player = new SimplePlayerPermissiblePermissible((String) entry.getKey());
        loadPermissionHolderNode(player, entry.getValue());
        this.players.put(player.getUuid(), player);
      }
      for (final var entry : root.node("groups").childrenMap().entrySet()) {
        final var group = new SimpleGroupPermissiblePermissible((String) entry.getKey());
        loadPermissionHolderNode(group, entry.getValue());
        group.setWeight(entry.getValue().node("weight").getInt());
        this.groups.put(group.getName(), group);
      }
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Unable to load the permissions.", e);
    }
  }

  @Override
  public boolean hasPermission(String uuid, String permission) {
    if (verifyAdmin && Vars.netServer.admins.getInfoOptional(uuid).admin) {
      return true;
    }

    permission = permission.toLowerCase(Locale.ROOT);
    var state = Tristate.UNDEFINED;
    final var visited = new HashSet<String>();
    final var queue = new ArrayDeque<Permissible>();

    if (players.containsKey(uuid)) {
      queue.add(players.get(uuid));
    } else if (groups.containsKey(primaryGroup)) {
      queue.add(groups.get(primaryGroup));
    } else {
      return false;
    }

    while (!queue.isEmpty()) {
      final var holder = queue.remove();
      state = holder.getPermission(permission);
      if (state != Tristate.UNDEFINED) {
        break;
      }
      holder.getParentGroups()
        .stream()
        .filter(g -> visited.add(g) && groups.containsKey(g))
        .map(groups::get)
        .sorted(GROUP_COMPARATOR)
        .forEach(queue::add);
      if (queue.isEmpty() && !visited.add(primaryGroup) && groups.containsKey(primaryGroup)) {
        queue.add(groups.get(primaryGroup));
      }
    }
    return state.asBoolean();
  }

  @Override
  public PlayerPermissible getPlayerPermissible(String uuid) {
    return players.containsKey(uuid) ? players.get(uuid) : new SimplePlayerPermissiblePermissible(uuid);
  }

  @Override
  public boolean existsPlayerPermissibleByUuid(String uuid) {
    return players.containsKey(uuid);
  }

  @Override
  public void savePlayerPermissible(PlayerPermissible player) {
    players.put(player.getUuid(), player);
    save();
  }

  @Override
  public List<PlayerPermissible> getAllPlayerPermissible() {
    return List.copyOf(players.values());
  }

  @Override
  public void deletePlayerPermissibleByUuid(String uuid) {
    if (players.remove(uuid) != null) {
      save();
    }
  }

  @Override
  public GroupPermissible getGroupPermissible(String group) {
    return groups.containsKey(group) ? groups.get(group) : new SimpleGroupPermissiblePermissible(group);
  }

  @Override
  public boolean existsGroupPermissibleByName(String name) {
    return groups.containsKey(name);
  }

  @Override
  public void saveGroupPermissible(GroupPermissible group) {
    groups.put(group.getName(), group);
    save();
  }

  @Override
  public List<GroupPermissible> getAllGroupPermissible() {
    return List.copyOf(groups.values());
  }

  @Override
  public void deleteGroupPermissibleByName(String name) {
    if (groups.remove(name) != null) {
      save();
    }
  }

  @Override
  public GroupPermissible getPrimaryGroup() {
    return getGroupPermissible(primaryGroup);
  }

  @Override
  public void setPrimaryGroup(GroupPermissible group) {
    this.primaryGroup = group.getName();
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

  private void save() {
    try {
      final var root = this.loader.createNode();
      root.node("primary-group").set(primaryGroup);
      root.node("verify-admin").set(verifyAdmin);
      for (final var player : this.players.values()) {
        final var node = root.node("players", player.getUuid());
        savePermissionHolderData(player, node);
      }
      for (final var group : this.groups.values()) {
        final var node = root.node("groups", group.getName());
        savePermissionHolderData(group, node);
        node.node("weight").set(group.getWeight());
      }
      this.loader.save(root);
    } catch (final IOException e) {
      throw new RuntimeException("Failed to save the permissions.", e);
    }
  }

   */
}
