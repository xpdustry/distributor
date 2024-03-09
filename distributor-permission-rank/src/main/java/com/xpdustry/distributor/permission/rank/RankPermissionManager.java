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

import com.xpdustry.distributor.common.permission.PermissionManager;
import com.xpdustry.distributor.common.permission.TriState;
import mindustry.gen.Player;

final class RankPermissionManager implements PermissionManager {

    private final RankProvider provider;
    private final RankPermissionStorage storage;

    RankPermissionManager(final RankProvider provider, final RankPermissionStorage storage) {
        this.provider = provider;
        this.storage = storage;
    }

    @Override
    public TriState getPermission(final Player player, final String permission) {
        for (final var node : this.provider.getRanks(player)) {
            RankNode current = node;
            while (current != null) {
                final var state = this.storage.getRankPermissions(current).getPermission(permission);
                if (state != TriState.UNDEFINED) {
                    return state;
                }
                current = node.getPrevious();
            }
        }
        return TriState.UNDEFINED;
    }
}
