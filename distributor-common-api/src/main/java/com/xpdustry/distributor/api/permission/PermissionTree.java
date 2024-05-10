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
import java.util.regex.Pattern;

/**
 * A tree of permissions. A fancy radix tree if you will.
 */
public interface PermissionTree {

    /**
     * Regex pattern used to validate permission strings.
     * <p>
     * <strong>Notes:</strong>
     * <blockquote>
     * Permission strings are composed of a series of nodes separated by dots with alphanumeric characters and minus
     * signs, such as {@code "plugin.command"}.
     * <br>
     * A parent node is always overridden by a child node, such as {@code "plugin.command"} overriding {@code "plugin"}.
     * <br>
     * Wildcards are also allowed, but they currently have the same effect as a normal node, such as {@code "plugin.command.*"} equals {@code "plugin.command"}.
     * <br>
     * The only relevant use of wildcards is the root permission {@code "*"} permission. It
     * allows you to set a default value for all permissions.
     * </blockquote>
     */
    Pattern PERMISSION_PATTERN = Pattern.compile("^(\\*|\\w+)(\\.(\\*|\\w+))*$");

    /**
     * Returns an immutable empty permission tree.
     */
    static PermissionTree empty() {
        return EmptyPermissionTree.INSTANCE;
    }

    /**
     * Returns an immutable permission tree with all permissions.
     */
    static PermissionTree all() {
        final var tree = MutablePermissionTree.create();
        tree.setPermission("*", true);
        return new ImmutablePermissionTree(tree);
    }

    /**
     * Creates an immutable permission tree with the given tree.
     *
     * @param tree the tree
     * @return the immutable tree
     */
    static PermissionTree from(final PermissionTree tree) {
        if (tree instanceof ImmutablePermissionTree || tree instanceof EmptyPermissionTree) {
            return tree;
        }
        final var mutable = MutablePermissionTree.create();
        mutable.setPermissions(tree);
        return new ImmutablePermissionTree(mutable);
    }

    /**
     * Get the permission state for a given permission.
     *
     * @param permission the permission
     * @return the permission state
     */
    TriState getPermission(final String permission);

    /**
     * A flattened map of permissions.
     */
    Map<String, Boolean> getPermissions();
}
