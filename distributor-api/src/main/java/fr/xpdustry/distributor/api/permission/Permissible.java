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
package fr.xpdustry.distributor.api.permission;

import fr.xpdustry.distributor.api.util.*;
import java.util.*;
import java.util.regex.*;

public interface Permissible {

  String PERMISSION_REGEX = "^(\\*|[a-z\\d\\-]+)(\\.(\\*|[a-z\\d\\-]+))*$";
  Pattern PERMISSION_PATTERN = Pattern.compile(PERMISSION_REGEX);

  String getName();

  Tristate getPermission(final String permission);

  void setPermission(final String permission, final Tristate state);

  Map<String, Boolean> getPermissions();

  void setPermissions(final Map<String, Boolean> permissions);

  Collection<String> getParentGroups();

  void setParents(final Collection<String> parents);

  void addParent(final String group);

  void removeParent(final String group);
}
