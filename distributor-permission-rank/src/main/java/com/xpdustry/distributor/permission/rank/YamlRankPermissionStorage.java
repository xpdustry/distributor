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
package com.xpdustry.distributor.permission.rank;

import arc.util.Log;
import com.xpdustry.distributor.common.permission.PermissionTree;
import com.xpdustry.distributor.common.permission.TriState;
import com.xpdustry.distributor.common.plugin.PluginListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import mindustry.server.ServerControl;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

final class YamlRankPermissionStorage implements RankPermissionStorage, PluginListener {

    private Map<String, PermissionTree> permissions = Collections.emptyMap();
    private final Object lock = new Object();
    private final YamlConfigurationLoader loader;

    YamlRankPermissionStorage(final Path file) {
        this.loader = YamlConfigurationLoader.builder()
                .path(file)
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

        ServerControl.instance.handler.register("rank-permission-reload", "Reload the permissions", $ -> {
            try {
                reload();
                Log.info("Reloaded rank permissions file");
            } catch (final IOException e) {
                Log.err("Failed to reload rank permission file", e);
            }
        });
    }

    @Override
    public PermissionTree getRankPermissions(final RankNode node) {
        synchronized (this.lock) {
            return this.permissions.getOrDefault(node.getName().toLowerCase(Locale.ROOT), PermissionTree.empty());
        }
    }

    private void reload() throws IOException {
        final Map<String, PermissionTree> map = new HashMap<>();
        for (final var node : loader.load().node("ranks").childrenList()) {
            final var name = node.node("name").getString();
            if (name == null || name.isBlank()) {
                throw new IOException("Invalid rank name.");
            }
            final var tree = PermissionTree.create();
            for (final var entry : node.node("permissions").childrenMap().entrySet()) {
                tree.setPermission(
                        (String) entry.getKey(), TriState.of(entry.getValue().getBoolean()));
            }
            map.put(name, PermissionTree.immutable(tree));
        }
        synchronized (this.lock) {
            this.permissions = map;
        }
    }
}
