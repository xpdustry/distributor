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

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.permission.PlayerPermissionProvider;
import com.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class DistributorPermissionRankPlugin extends AbstractMindustryPlugin {

    @Override
    public void onInit() {
        final var services = DistributorProvider.get().getServiceManager();

        services.register(this, PlayerPermissionProvider.class, new RankPlayerPermissionProvider(), Priority.HIGH);
        services.register(this, RankProvider.class, new MindustryRankProvider(), Priority.LOW);
        final var yaml = new YamlRankPermissionSource(() -> Files.newBufferedReader(this.getConfigFile()));
        this.addListener(yaml);
        services.register(this, RankPermissionSource.class, yaml, Priority.NORMAL);
        services.register(this, RankPermissionSource.class, new MindustryRankPermissionSource(), Priority.LOW);

        this.getLogger().info("Initialized distributor permission rank plugin");
    }

    private Path getConfigFile() throws IOException {
        final var path = this.getDirectory().resolve("permissions.yaml");
        if (Files.notExists(path)) {
            try (final var stream = Objects.requireNonNull(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("com/xpdustry/distributor/api/permission/rank/default.yaml"))) {
                Files.copy(stream, path);
            } catch (final IOException error) {
                throw new IOException("Failed to create the default permission file", error);
            }
        }
        return path;
    }
}
