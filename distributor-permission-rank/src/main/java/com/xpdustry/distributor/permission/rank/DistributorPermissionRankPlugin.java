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

import com.xpdustry.distributor.DistributorProvider;
import com.xpdustry.distributor.permission.PermissionReader;
import com.xpdustry.distributor.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.util.Priority;

public final class DistributorPermissionRankPlugin extends AbstractMindustryPlugin {

    @Override
    public void onInit() {
        final var services = DistributorProvider.get().getServiceManager();

        services.register(this, RankSource.class, Priority.LOW, new MindustryRankSource());

        final var source = new YamlRankPermissionSource(this.getDirectory().resolve("permissions.yaml"));
        this.addListener(source);
        services.register(this, RankPermissionSource.class, Priority.LOW, source);

        services.register(this, PermissionReader.class, Priority.HIGH, new RankPermissionReader());
    }
}
