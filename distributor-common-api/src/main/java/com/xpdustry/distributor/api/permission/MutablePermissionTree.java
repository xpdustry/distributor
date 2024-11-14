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

import com.xpdustry.distributor.api.util.TriState;

/**
 * A mutable permission tree.
 */
public interface MutablePermissionTree extends PermissionTree {

    /**
     * Creates a new mutable permission tree.
     */
    static MutablePermissionTree create() {
        return new MutablePermissionTreeImpl();
    }

    /**
     * Sets the permission with the given state.
     *
     * @param permission the permission
     * @param state      the state
     */
    default void setPermission(final String permission, final boolean state) {
        setPermission(permission, state, false);
    }

    /**
     * Sets the permission with the given state.
     *
     * @param permission the permission
     * @param state      the state
     * @param override   whether to override the child permissions with the new state
     */
    void setPermission(final String permission, final boolean state, final boolean override);

    /**
     * Sets the permissions from the given permission tree.
     *
     * @param tree the permission tree
     */
    default void setPermissions(final PermissionTree tree) {
        setPermissions(tree, false);
    }

    /**
     * Sets the permissions from the given permission tree.
     *
     * @param tree     the permission tree
     * @param override whether to override the child permissions with the new state
     */
    default void setPermissions(final PermissionTree tree, final boolean override) {
        for (final var entry : tree.getPermissions().entrySet()) {
            setPermission(entry.getKey(), entry.getValue(), override);
        }
    }

    /**
     * Removes the given permission from the tree. Essentially making it {@link TriState#UNDEFINED}.
     *
     * @param permission the permission
     */
    default void removePermission(final String permission) {
        removePermission(permission, false);
    }

    /**
     * Removes the given permission from the tree. Essentially making it {@link TriState#UNDEFINED}.
     *
     * @param permission the permission
     * @param all        whether to remove all child permissions
     */
    void removePermission(final String permission, final boolean all);

    /**
     * Clears all permissions.
     */
    default void clearPermissions() {
        removePermission("*", true);
    }
}
