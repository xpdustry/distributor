/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.api.permission.rank;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.permission.MutablePermissionTree;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import com.xpdustry.distributor.api.permission.PermissionTree;
import com.xpdustry.distributor.api.plugin.PluginListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

final class YamlRankPermissionSource implements RankPermissionSource, PluginListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlRankPermissionSource.class);
    private Map<String, PermissionTree> permissions = Collections.emptyMap();
    private final Map<String, PermissionTree> cache = new HashMap<>();
    private final Object lock = new Object();
    private final YamlConfigurationLoader loader;

    YamlRankPermissionSource(final Callable<BufferedReader> source) {
        this.loader = YamlConfigurationLoader.builder()
                .source(source)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
    }

    @Override
    public void onPluginLoad() {
        try {
            reload();
        } catch (final IOException error) {
            throw new RuntimeException("Failed to load permission file.", error);
        }
    }

    @Override
    public void onPluginServerCommandsRegistration(final CommandHandler handler) {
        handler.register("rank-permission-reload", "Reload the permissions", $ -> {
            try {
                reload();
                LOGGER.info("Reloaded rank permissions file");
            } catch (final IOException e) {
                LOGGER.error("Failed to reload rank permission file", e);
            }
        });
    }

    @Override
    public PermissionContainer getRankPermissions(final RankNode node) {
        synchronized (this.lock) {
            return getRankPermissions0(node, new HashSet<>());
        }
    }

    private PermissionTree getRankPermissions0(final RankNode node, final Set<RankNode> visited) {
        final var name = node.getName().toLowerCase(Locale.ROOT);
        if (!RankNode.NAME_PATTERN.matcher(name).matches()) {
            return PermissionTree.empty();
        }
        final var cached = this.cache.get(name);
        if (cached != null) {
            return cached;
        }
        final var tree = MutablePermissionTree.create();
        final var previous = node.getPrevious();
        if (previous != null) {
            if (!visited.add(node)) {
                throw new IllegalStateException("Circular rank node: " + name);
            }
            tree.setPermissions(getRankPermissions0(previous, visited));
        }
        final var permissions = this.permissions.get(name);
        if (permissions != null) {
            tree.setPermissions(permissions, true);
        }
        final var immutable = PermissionTree.immutable(tree);
        this.cache.put(name, immutable);
        return immutable;
    }

    void reload() throws IOException {
        final Map<String, PermissionTree> map = new HashMap<>();
        final var root = this.loader.load();
        for (final var rank : root.node("ranks").childrenMap().entrySet()) {
            final var name = (String) rank.getKey();
            if (name.isBlank() || !RankNode.NAME_PATTERN.matcher(name).matches()) {
                throw new IOException("Invalid rank name: " + name);
            }
            final var tree = MutablePermissionTree.create();
            for (final var permission : rank.getValue().childrenMap().entrySet()) {
                tree.setPermission(
                        (String) permission.getKey(), permission.getValue().getBoolean());
            }
            map.put(name, PermissionTree.immutable(tree));
        }
        synchronized (this.lock) {
            this.permissions = map;
            this.cache.clear();
        }
    }
}
