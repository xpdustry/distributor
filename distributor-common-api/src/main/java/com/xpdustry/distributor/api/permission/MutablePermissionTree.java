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

public interface MutablePermissionTree extends PermissionTree {

    static MutablePermissionTree create() {
        return new MutablePermissionTreeImpl();
    }

    static MutablePermissionTree from(final PermissionTree tree) {
        final var mutable = create();
        mutable.setPermissions(tree);
        return mutable;
    }

    default void setPermission(final String permission, final boolean state) {
        setPermission(permission, state, false);
    }

    void setPermission(final String permission, final boolean state, final boolean override);

    default void setPermissions(final PermissionTree tree) {
        setPermissions(tree, false);
    }

    default void setPermissions(final PermissionTree tree, final boolean override) {
        for (final var entry : tree.getPermissions().entrySet()) {
            setPermission(entry.getKey(), entry.getValue(), override);
        }
    }

    void clearPermissions();

    void removePermission(final String permission);
}
