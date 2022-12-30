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
package fr.xpdustry.distributor.core.permission;

import fr.xpdustry.distributor.api.permission.Permissible;
import fr.xpdustry.distributor.api.util.Tristate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPermissible implements Permissible {

    private final Set<String> parents = new HashSet<>();
    private final PermissionTree tree = new PermissionTree();

    @Override
    public Collection<String> getParentGroups() {
        return Collections.unmodifiableCollection(this.parents);
    }

    @Override
    public void setParents(final Collection<String> parents) {
        this.parents.clear();
        this.parents.addAll(parents);
    }

    @Override
    public void addParent(final String group) {
        this.parents.add(group);
    }

    @Override
    public void removeParent(final String group) {
        this.parents.remove(group);
    }

    @Override
    public Tristate getPermission(final String permission) {
        return this.tree.getPermission(permission);
    }

    @Override
    public void setPermission(final String permission, final Tristate state) {
        this.tree.setPermission(permission, state);
    }

    @Override
    public Map<String, Boolean> getPermissions() {
        return this.tree.getPermissions();
    }

    @Override
    public void setPermissions(final Map<String, Boolean> permissions) {
        permissions.forEach((permission, state) -> this.setPermission(permission, Tristate.of(state)));
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final AbstractPermissible that)) {
            return false;
        }

        if (!this.parents.equals(that.parents)) {
            return false;
        }
        return this.tree.equals(that.tree);
    }

    @Override
    public int hashCode() {
        int result = this.parents.hashCode();
        result = 31 * result + this.tree.hashCode();
        return result;
    }
}
