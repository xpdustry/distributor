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
import com.xpdustry.distributor.api.permission.PermissionReader;
import com.xpdustry.distributor.api.permission.TriState;
import java.util.HashSet;
import mindustry.gen.Player;

final class RankPermissionReader implements PermissionReader {

    @Override
    public TriState getPermission(final Player player, final String permission) {
        final var services = DistributorProvider.get().getServiceManager();
        final var sources = services.getProviders(RankPermissionSource.class);
        for (final var provider : services.getProviders(RankSource.class)) {
            for (final var node : provider.getInstance().getRanks(player)) {
                final var visited = new HashSet<RankNode>();
                for (final var source : sources) {
                    RankNode current = node;
                    while (current != null && visited.add(current)) {
                        final var state =
                                source.getInstance().getRankPermissions(current).getPermission(permission);
                        if (state != TriState.UNDEFINED) {
                            return state;
                        }
                        current = node.getPrevious();
                    }
                }
            }
        }
        return TriState.UNDEFINED;
    }
}