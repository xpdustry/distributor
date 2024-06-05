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

import java.util.Map;

/**
 * A tree of permissions. A fancy radix tree if you will.
 */
public interface PermissionTree extends PermissionContainer {

    /**
     * Returns an immutable empty permission tree.
     */
    static PermissionTree empty() {
        return ImmutablePermissionTree.EMPTY;
    }

    /**
     * Creates an immutable permission tree with the given tree.
     *
     * @param tree the tree
     * @return the immutable tree
     */
    static PermissionTree from(final PermissionTree tree) {
        if (tree instanceof ImmutablePermissionTree) return tree;
        final var mutable = MutablePermissionTree.create();
        mutable.setPermissions(tree);
        return new ImmutablePermissionTree(mutable);
    }

    /**
     * A flattened map of permissions.
     */
    Map<String, Boolean> getPermissions();
}
