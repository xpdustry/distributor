/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.security.permission;

import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;

/**
 * A service that manages permissions.
 */
public interface PermissionService {

    /**
     * Looks up the permission of a player. Taking into account:
     * <ul>
     *     <li>the player's parent groups</li>
     *     <li>the player's validation status</li>
     * </ul>
     *
     * @param muuid the player's muuid.
     * @return the state of the permission for the player.
     */
    Tristate getPlayerPermission(final MUUID muuid, final String permission);

    /**
     * Looks up the permission of a player. Taking into account:
     * <ul>
     *     <li>the player's parent groups</li>
     * </ul>
     *
     * @param uuid the player's uuid.
     * @return the state of the permission for the player.
     */
    Tristate getPlayerPermission(final String uuid, final String permission);

    /**
     * Looks up the permission of a group. Taking into account:
     * <ul>
     *     <li>the group's parent groups</li>
     * </ul>
     *
     * @param group the group's name.
     * @return the state of the permission for the group.
     */
    Tristate getGroupPermission(final String group, final String permission);

    /**
     * Returns the permissible manager for players.
     */
    PermissibleManager<PlayerPermissible> getPlayerPermissionManager();

    /**
     * Returns the permissible manager for groups.
     */
    PermissibleManager<GroupPermissible> getGroupPermissionManager();
}
