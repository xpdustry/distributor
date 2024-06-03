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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MutablePermissionTreeImpl implements MutablePermissionTree {

    private final @Nullable MutablePermissionTreeImpl parent;
    private final Map<String, MutablePermissionTreeImpl> children = new HashMap<>();
    private TriState value = TriState.UNDEFINED;

    MutablePermissionTreeImpl() {
        this.parent = null;
    }

    private MutablePermissionTreeImpl(final @Nullable MutablePermissionTreeImpl parent) {
        this.parent = parent;
    }

    @Override
    public TriState getPermission(final String permission) {
        checkPermission(permission);
        var state = TriState.UNDEFINED;
        var node = this;
        for (final var part : permission.split("\\.", -1)) {
            final var wildcard = node.children.get("*");
            if (wildcard != null && wildcard.value != TriState.UNDEFINED) {
                state = wildcard.value;
            }
            node = node.children.get(part);
            if (node == null) {
                return state;
            } else if (node.value != TriState.UNDEFINED) {
                state = node.value;
            }
        }
        return state;
    }

    @Override
    public Map<String, Boolean> getPermissions() {
        final Map<String, Boolean> permissions = new HashMap<>();
        for (final var child : this.children.entrySet()) {
            if (child.getValue().value != TriState.UNDEFINED) {
                permissions.put(child.getKey(), child.getValue().value.asBoolean());
            }
            for (final var entry : child.getValue().getPermissions().entrySet()) {
                permissions.put(child.getKey() + "." + entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(permissions);
    }

    @Override
    public void setPermission(final String permission, final boolean state, final boolean override) {
        checkPermission(permission);
        final var parts = permission.split("\\.", -1);
        var node = this;
        for (final var part : parts) {
            final var parent = node;
            node = node.children.computeIfAbsent(part, k -> new MutablePermissionTreeImpl(parent));
        }
        node.value = TriState.of(state);
        if (override) {
            node.children.clear();
        }
    }

    @Override
    public void removePermission(final String permission, final boolean all) {
        checkPermission(permission);
        final var parts = permission.split("\\.", -1);
        var node = this;
        for (final var part : parts) {
            node = node.children.get(part);
            if (node == null) {
                return;
            }
        }
        node.value = TriState.UNDEFINED;
        if (all) {
            node.children.clear();
        }
        var index = parts.length - 1;
        while (node.parent != null && node.children.isEmpty()) {
            node = node.parent;
            node.children.remove(parts[index--]);
        }
    }

    @Override
    public void clearPermissions() {
        this.children.clear();
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final MutablePermissionTreeImpl that)) {
            return false;
        }
        if (!this.children.equals(that.children)) {
            return false;
        }
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.children, this.value);
    }

    private void checkPermission(final String permission) {
        if (!PermissionTree.PERMISSION_PATTERN.matcher(permission).matches()) {
            throw new IllegalArgumentException("The permission is not valid: " + permission);
        }
    }
}
