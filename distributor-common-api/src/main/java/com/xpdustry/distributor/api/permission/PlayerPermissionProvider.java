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

/**
 * A permission provider for online players.
 */
@FunctionalInterface
public interface PlayerPermissionProvider {

    /**
     * Returns a permission provider backed by Mindustry admin permissions.
     */
    static PlayerPermissionProvider mindustry() {
        return MindustryPlayerPermissionProvider.INSTANCE;
    }

    /**
     * Gets the permission of the given player.
     *
     * @param player the player
     * @return the player's permission
     */
    PermissionContainer getPermissions(final Player player);
}
