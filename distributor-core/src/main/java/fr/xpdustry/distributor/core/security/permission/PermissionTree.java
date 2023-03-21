/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.core.security.permission;

import fr.xpdustry.distributor.api.security.permission.Permissible;
import fr.xpdustry.distributor.api.util.Tristate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PermissionTree {

    private final @Nullable PermissionTree parent;
    private final Map<String, PermissionTree> children = new HashMap<>();
    private Tristate value = Tristate.UNDEFINED;

    public PermissionTree() {
        this.parent = null;
    }

    private PermissionTree(final @Nullable PermissionTree parent) {
        this.parent = parent;
    }

    public Tristate getPermission(final String permission) {
        if (!Permissible.PERMISSION_PATTERN.matcher(permission).matches()) {
            throw new IllegalArgumentException("The permission doesn't match the regex: " + permission);
        }
        var state = Tristate.UNDEFINED;
        var node = this;
        for (final var part : permission.split("\\.", -1)) {
            if (node.children.containsKey("*") && node.children.get("*").value != Tristate.UNDEFINED) {
                state = node.children.get("*").value;
            }
            node = node.children.get(part);
            if (node == null) {
                return state;
            } else if (node.value != Tristate.UNDEFINED) {
                state = node.value;
            }
        }
        return state;
    }

    public void setPermission(final String permission, final Tristate state) {
        if (!Permissible.PERMISSION_PATTERN.matcher(permission).matches()) {
            throw new IllegalArgumentException("The permission doesn't match the regex: " + permission);
        }
        final var parts = permission.split("\\.", -1);
        var node = this;
        if (state != Tristate.UNDEFINED) {
            for (final var part : parts) {
                final var parent = node;
                node = node.children.computeIfAbsent(part, k -> new PermissionTree(parent));
            }
            node.value = state;
        } else {
            for (final var part : parts) {
                node = node.children.get(part);
                if (node == null) {
                    return;
                }
            }
            node.value = state;
            var index = parts.length - 1;
            while (node.parent != null && node.children.size() == 0) {
                node = node.parent;
                node.children.remove(parts[index--]);
            }
        }
    }

    public Map<String, Boolean> getPermissions() {
        final Map<String, Boolean> permissions = new HashMap<>();
        for (final var child : this.children.entrySet()) {
            if (child.getValue().value != Tristate.UNDEFINED) {
                permissions.put(child.getKey(), child.getValue().value.asBoolean());
            }
            for (final var entry : child.getValue().getPermissions().entrySet()) {
                permissions.put(child.getKey() + "." + entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(permissions);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionTree that)) {
            return false;
        }
        if (!this.children.equals(that.children)) {
            return false;
        }
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        int result = this.children.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}
