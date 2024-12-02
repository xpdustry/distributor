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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import com.xpdustry.distributor.api.permission.PlayerPermissionProvider;
import com.xpdustry.distributor.api.util.TriState;
import mindustry.gen.Player;

final class RankPlayerPermissionProvider implements PlayerPermissionProvider {

    @Override
    public PermissionContainer getPermissions(final Player player) {
        return permission -> {
            final var services = Distributor.get().getServiceManager();
            final var permissionSources = services.getProviders(RankPermissionSource.class);
            final var rankSource = services.provide(RankProvider.class).orElseThrow();
            for (final var rank : rankSource.getRanks(player)) {
                for (final var permissionSource : permissionSources) {
                    final var value = permissionSource
                            .getInstance()
                            .getRankPermissions(rank)
                            .getPermission(permission);
                    if (value != TriState.UNDEFINED) {
                        return value;
                    }
                }
            }
            return TriState.UNDEFINED;
        };
    }
}
