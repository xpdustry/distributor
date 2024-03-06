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

import com.xpdustry.distributor.core.DistributorProvider;
import com.xpdustry.distributor.core.permission.PermissionManager;
import com.xpdustry.distributor.core.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.core.util.Priority;

public final class DistributorPermissionRankPlugin extends AbstractMindustryPlugin {

    @Override
    public void onInit() {
        final var services = DistributorProvider.get().getServiceManager();
        services.register(this, RankProvider.class, Priority.LOW, MindustryRankProvider::new);
        services.register(this, RankPermissionStorage.class, Priority.LOW, () -> {
            final var storage =
                    new YamlRankPermissionStorage(this.getDirectory().resolve("permissions.yaml"));
            this.addListener(storage);
            return storage;
        });
        services.register(
                this,
                PermissionManager.class,
                Priority.HIGH,
                () -> new RankPermissionManager(
                        services.provide(RankProvider.class), services.provide(RankPermissionStorage.class)));
    }
}
