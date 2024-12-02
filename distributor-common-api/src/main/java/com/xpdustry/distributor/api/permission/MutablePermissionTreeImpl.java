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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MutablePermissionTreeImpl implements MutablePermissionTree {

    final @Nullable MutablePermissionTreeImpl parent;
    final Map<String, MutablePermissionTreeImpl> children = new HashMap<>();
    private TriState value = TriState.UNDEFINED;

    MutablePermissionTreeImpl() {
        this.parent = null;
    }

    private MutablePermissionTreeImpl(final @Nullable MutablePermissionTreeImpl parent) {
        this.parent = parent;
    }

    @Override
    public TriState getPermission(String permission) {
        permission = permission.toLowerCase(Locale.ROOT);
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
    public void setPermission(String permission, final TriState state, boolean override) {
        permission = permission.toLowerCase(Locale.ROOT);
        checkPermission(permission);
        final var parts = permission.split("\\.", -1);
        var node = this;
        if (state != TriState.UNDEFINED) {
            for (final var part : parts) {
                final var parent = node;
                node = node.children.computeIfAbsent(part, k -> new MutablePermissionTreeImpl(parent));
            }
            node.value = state;
            if (override) {
                node.children.clear();
            }
        } else {
            for (final var part : parts) {
                node = node.children.get(part);
                if (node == null) {
                    return;
                }
            }
            node.value = TriState.UNDEFINED;
            if (override) {
                node.children.clear();
            }
            var index = parts.length - 1;
            while (node.parent != null && node.children.isEmpty()) {
                node = node.parent;
                node.children.remove(parts[index--]);
            }
        }
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return this == o
                || (o instanceof MutablePermissionTreeImpl that
                        && this.children.equals(that.children)
                        && this.value == that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.children, this.value);
    }

    @Override
    public String toString() {
        return "MutablePermissionTreeImpl{children=" + this.children + ", value=" + this.value + '}';
    }

    private void checkPermission(final String permission) {
        if (!PermissionContainer.isValidPermission(permission)) {
            throw new IllegalArgumentException("The permission is not valid: " + permission);
        }
    }
}
