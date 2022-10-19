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

import java.nio.file.*;
import org.spongepowered.configurate.*;

public final class SimpleGroupPermissibleManager extends AbstractPermissibleManager<GroupPermissible> implements GroupPermissibleManager {

  public SimpleGroupPermissibleManager(final Path path) {
    super(path);
  }

  @Override
  void loadPermissibleData(GroupPermissible permissible, ConfigurationNode node) throws ConfigurateException {
    permissible.setWeight(node.node("weight").getInt());
    super.loadPermissibleData(permissible, node);
  }

  @Override
  void savePermissibleData(GroupPermissible permissible, ConfigurationNode node) throws ConfigurateException {
    node.node("weight").set(permissible.getWeight());
    super.savePermissibleData(permissible, node);
  }

  @Override
  protected GroupPermissible createPermissible(String id) {
    return new SimpleGroupPermissible(id);
  }

  @Override
  protected String extractId(GroupPermissible permissible) {
    return permissible.getName();
  }
}
