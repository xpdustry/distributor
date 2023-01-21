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
package fr.xpdustry.distributor.api.permission;

import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;

/**
 * A service that manages permissions.
 */
public interface PermissionService {

    /**
     * Looks up the permission of a player. Taking into account the player's groups.
     *
     * @param muuid the player's muuid.
     * @return the state of the permission for the player.
     */
    Tristate getPermission(final MUUID muuid, final String permission);

    /**
     * Returns the primary group of all players.
     */
    String getPrimaryGroup();

    /**
     * Sets the primary group of all players.
     */
    void setPrimaryGroup(final String group);

    /**
     * Returns whether permission checking is skipped for a player if it's an admin.
     */
    boolean getVerifyAdmin();

    /**
     * Sets whether permission checking is skipped for a player if it's an admin.
     */
    void setVerifyAdmin(final boolean verify);

    /**
     * Returns the permissible manager for players.
     */
    PermissibleManager<PlayerPermissible> getPlayerPermissionManager();

    /**
     * Returns the permissible manager for groups.
     */
    PermissibleManager<GroupPermissible> getGroupPermissionManager();

    /**
     * Returns the identity validator.
     */
    IdentityValidator getIdentityValidator();
}
