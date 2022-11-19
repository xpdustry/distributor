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

import fr.xpdustry.distributor.api.permission.Permissible;
import fr.xpdustry.distributor.api.permission.PermissibleManager;
import fr.xpdustry.distributor.api.util.Magik;
import fr.xpdustry.distributor.api.util.Tristate;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public abstract class AbstractPermissibleManager<P extends Permissible> implements PermissibleManager<P> {

    private final Map<String, P> permissibles = new HashMap<>();
    private final YamlConfigurationLoader loader;

    protected AbstractPermissibleManager(final Path path) {
        final var extension = Magik.getFileExtension(path);
        if (extension.isEmpty()
                || !(extension.get().equals("yml") || extension.get().equals("yaml"))) {
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
                final var permissible = this.createPermissible((String) entry.getKey());
                this.loadPermissibleData(permissible, entry.getValue());
                this.permissibles.put(this.extractId(permissible), permissible);
            }
        } catch (final ConfigurateException e) {
            throw new RuntimeException("Unable to load the permissions.", e);
        }
    }

    @Override
    public void save(final P entity) {
        this.permissibles.put(this.extractId(entity), entity);
        this.save();
    }

    @Override
    public void saveAll(final Iterable<P> entities) {
        entities.forEach(this::save);
        this.save();
    }

    @Override
    public P findOrCreateById(final String id) {
        return this.permissibles.containsKey(id) ? this.permissibles.get(id) : this.createPermissible(id);
    }

    @Override
    public Optional<P> findById(final String id) {
        return Optional.ofNullable(this.permissibles.get(id));
    }

    @Override
    public Iterable<P> findAll() {
        return List.copyOf(this.permissibles.values());
    }

    @Override
    public boolean exists(final P entity) {
        return this.existsById(this.extractId(entity));
    }

    @Override
    public long count() {
        return this.permissibles.size();
    }

    @Override
    public void deleteById(final String id) {
        if (this.permissibles.remove(id) != null) {
            this.save();
        }
    }

    @Override
    public void delete(final P entity) {
        this.deleteById(this.extractId(entity));
    }

    @Override
    public void deleteAll(final Iterable<P> entities) {
        var changed = false;
        for (final var entity : entities) {
            changed |= this.permissibles.remove(this.extractId(entity)) != null;
        }
        if (changed) {
            this.save();
        }
    }

    @Override
    public void deleteAll() {
        this.permissibles.clear();
        this.save();
    }

    void loadPermissibleData(final P permissible, final ConfigurationNode node) throws ConfigurateException {
        for (final var parent : node.node("parents").getList(String.class, Collections.emptyList())) {
            permissible.addParent(parent);
        }
        for (final var entry : node.node("permissions").childrenMap().entrySet()) {
            permissible.setPermission(
                    (String) entry.getKey(), Tristate.of(entry.getValue().getBoolean()));
        }
    }

    void savePermissibleData(final P permissible, final ConfigurationNode node) throws ConfigurateException {
        for (final var parent : permissible.getParentGroups()) {
            node.node("parents").appendListNode().set(parent);
        }
        for (final var permission : permissible.getPermissions().entrySet()) {
            node.node("permissions", permission.getKey()).set(permission.getValue());
        }
    }

    private void save() {
        try {
            final var root = this.loader.createNode();
            for (final var permissible : this.permissibles.values()) {
                final var node = root.node(this.extractId(permissible));
                this.savePermissibleData(permissible, node);
            }
            this.loader.save(root);
        } catch (final ConfigurateException e) {
            throw new RuntimeException("Failed to save the permissions.", e);
        }
    }

    protected abstract String extractId(final P permissible);

    protected abstract P createPermissible(final String id);
}
