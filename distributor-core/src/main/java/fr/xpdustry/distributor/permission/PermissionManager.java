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

import java.util.*;

public interface PermissionManager {

  // TODO Divide groups and users between 2 managers

  boolean hasPermission(final String uuid, final String permission);

  PermissionPlayer getPlayerPermissible(final String uuid);

  void savePlayerPermissible(final PermissionPlayer player);

  List<PermissionPlayer> getAllPlayerPermissible();

  void deletePlayerPermissibleByUuid(final String uuid);

  default void deletePlayerPermissible(final PermissionPlayer player) {
    deletePlayerPermissibleByUuid(player.getUuid());
  }

  PermissionGroup getGroupPermissible(final String group);

  void saveGroupPermissible(final PermissionGroup group);

  List<PermissionGroup> getAllGroupPermissible();

  void deleteGroupPermissibleByName(final String name);

  default void deleteGroupPermissible(final PermissionGroup group) {
    deleteGroupPermissibleByName(group.getName());
  }

  PermissionGroup getPrimaryGroup();

  void setPrimaryGroup(final PermissionGroup group);

  boolean getVerifyAdmin();

  void setVerifyAdmin(final boolean status);
}
