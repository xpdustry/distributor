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

import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.util.UUID;

public final class SQLGroupPermissibleManagerTest extends AbstractSQLPermissibleManagerTest<GroupPermissible> {

    @Override
    protected PermissibleManager<GroupPermissible> createManager(final ConnectionFactory factory) {
        return new SQLGroupPermissibleManager(factory);
    }

    @Override
    protected GroupPermissible createRandomPermissible() {
        return new SimpleGroupPermissible(UUID.randomUUID().toString());
    }

    @Override
    protected String extractIdentifier(final GroupPermissible permissible) {
        return permissible.getName();
    }
}
