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
package com.xpdustry.distributor.api.permission;

import mindustry.gen.Player;

@FunctionalInterface
public interface PermissionProvider {

    static PermissionProvider empty() {
        return permission -> TriState.UNDEFINED;
    }

    static PermissionProvider all() {
        return permission -> TriState.TRUE;
    }

    static PermissionProvider from(final Player player) {
        return new PlayerPermissionProvider(player);
    }

    /**
     * Returns the permission state of the given permission.
     *
     * @param permission the permission to check
     * @return the permission state
     */
    TriState getPermission(final String permission);
}
