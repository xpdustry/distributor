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
import com.xpdustry.distributor.permission.PermissionManager;
import com.xpdustry.distributor.permission.TriState;
import java.util.HashSet;
import mindustry.gen.Player;

final class RankPermissionManager implements PermissionManager {

    @Override
    public TriState getPermission(final Player player, final String permission) {
        final var services = DistributorProvider.get().getServiceManager();
        final var storages = services.getProviders(RankPermissionStorage.class);
        for (final var provider : services.getProviders(RankProvider.class)) {
            for (final var node : provider.getInstance().getRanks(player)) {
                final var visited = new HashSet<RankNode>();
                for (final var storage : storages) {
                    RankNode current = node;
                    while (current != null && visited.add(current)) {
                        final var state = storage.getInstance()
                                .getRankPermissions(current)
                                .getPermission(permission);
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
