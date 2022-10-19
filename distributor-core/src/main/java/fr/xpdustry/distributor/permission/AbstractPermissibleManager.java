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
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.yaml.*;

abstract class AbstractPermissibleManager<E extends Permissible> implements PersistenceManager<E, String> {

  private final Map<String, E> permissibles = new HashMap<>();
  private final YamlConfigurationLoader loader;

  protected AbstractPermissibleManager(final Path path) {
    final var extension = Magik.getFileExtension(path);
    if (extension.isEmpty() || !(extension.get().equals("yml") || extension.get().equals("yaml"))) {
      throw new IllegalArgumentException("Unsupported file extension " + path);
    }

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
        permissibles.put(extractId(permissible), permissible);
      }
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Unable to load the permissions.", e);
    }
  }

  @Override
  public void save(E entity) {
    permissibles.put(extractId(entity), entity);
    save();
  }

  @Override
  public void saveAll(Iterable<E> entities) {
    entities.forEach(this::save);
    save();
  }

  @Override
  public E findOrCreateById(String id) {
    return permissibles.containsKey(id) ? permissibles.get(id) : createPermissible(id);
  }

  @Override
  public Optional<E> findById(String id) {
    return Optional.ofNullable(permissibles.get(id));
  }

  @Override
  public Iterable<E> findAll() {
    return List.copyOf(permissibles.values());
  }

  @Override
  public boolean exists(E entity) {
    return existsById(extractId(entity));
  }

  @Override
  public long count() {
    return permissibles.size();
  }

  @Override
  public void deleteById(String id) {
    if (permissibles.remove(id) != null) {
      save();
    }
  }

  @Override
  public void delete(E entity) {
    deleteById(extractId(entity));
  }

  @Override
  public void deleteAll(Iterable<E> entities) {
    var changed = false;
    for (final var entity : entities) {
      changed |= permissibles.remove(extractId(entity)) != null;
    }
    if (changed) {
      save();
    }
  }

  @Override
  public void deleteAll() {
    permissibles.clear();
    save();
  }

  void loadPermissibleData(final E permissible, final ConfigurationNode node) throws ConfigurateException {
    for (final var parent : node.node("parents").getList(String.class, Collections.emptyList())) {
      permissible.addParent(parent);
    }
    for (final var entry : node.node("permissions").childrenMap().entrySet()) {
      permissible.setPermission((String) entry.getKey(), Tristate.of(entry.getValue().getBoolean()));
    }
  }

  void savePermissibleData(final E permissible, final ConfigurationNode node) throws ConfigurateException {
    for (final var parent : permissible.getParentGroups()) {
      node.node("parents").appendListNode().set(parent);
    }
    for (final var permission : permissible.getPermissions().entrySet()) {
      node.node("permissions", permission.getKey()).set(permission.getValue());
    }
  }

  protected abstract String extractId(final E permissible);

  protected abstract E createPermissible(final String id);

  private void save() {
    try {
      final var root = loader.createNode();
      for (final var permissible : permissibles.values()) {
        final var node = root.node(extractId(permissible));
        savePermissibleData(permissible, node);
      }
      loader.save(root);
    } catch (final ConfigurateException e) {
      throw new RuntimeException("Failed to save the permissions.", e);
    }
  }
}
