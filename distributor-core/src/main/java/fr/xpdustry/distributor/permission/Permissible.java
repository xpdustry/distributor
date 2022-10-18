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
package fr.xpdustry.distributor.permission;

import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.regex.*;
import org.jetbrains.annotations.*;

public interface Permissible {

  String PERMISSION_REGEX = "^(\\*|[a-z\\d_\\-]+)(\\.(\\*|[a-z\\d_\\-]+))*$";
  Pattern PERMISSION_PATTERN = Pattern.compile(PERMISSION_REGEX);

  String getName();

  Tristate getPermission(final @NotNull String permission);

  void setPermission(final @NotNull String permission, final @NotNull Tristate state);

  Map<String, Boolean> getPermissions();

  void setPermissions(final Map<String, Boolean> permissions);

  Collection<String> getParentGroups();

  void setParents(final Collection<String> parents);

  void addParent(final String group);

  void removeParent(final String group);
}
