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

import fr.xpdustry.distributor.api.permission.*;
import fr.xpdustry.distributor.api.util.*;
import java.util.*;

public abstract class AbstractPermissionHolder implements PermissionHolder {

  private final Set<String> parents = new HashSet<>();
  private final PermissionTree tree = new PermissionTree();

  @Override
  public Collection<String> getParentGroups() {
    return Collections.unmodifiableCollection(parents);
  }

  @Override
  public void setParents(Collection<String> parents) {
    this.parents.clear();
    this.parents.addAll(parents);
  }

  @Override
  public void addParent(String group) {
    parents.add(group);
  }

  @Override
  public void removeParent(String group) {
    parents.remove(group);
  }

  @Override
  public Tristate getPermission(final String permission) {
    return tree.getPermission(permission);
  }

  @Override
  public void setPermission(String permission, Tristate state) {
    tree.setPermission(permission, state);
  }

  @Override
  public Map<String, Boolean> getPermissions() {
    return tree.getPermissions();
  }

  @Override
  public void setPermissions(final Map<String, Boolean> permissions) {
    permissions.forEach((permission, state) -> setPermission(permission, Tristate.of(state)));
  }
}
