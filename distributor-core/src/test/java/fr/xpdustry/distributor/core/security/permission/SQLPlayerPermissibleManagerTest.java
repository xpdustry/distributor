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
package fr.xpdustry.distributor.core.security.permission;

import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.util.Base64;
import java.util.Random;

public final class SQLPlayerPermissibleManagerTest extends AbstractSQLPermissibleManagerTest<PlayerPermissible> {

    private final Random random = new Random();

    @Override
    protected PermissibleManager<PlayerPermissible> createManager(final ConnectionFactory factory) {
        return new SQLPlayerPermissibleManager(factory);
    }

    @Override
    protected PlayerPermissible createRandomPermissible() {
        final var bytes = new byte[16];
        this.random.nextBytes(bytes);
        return new SimplePlayerPermissible(Base64.getEncoder().encodeToString(bytes));
    }

    @Override
    protected String extractIdentifier(final PlayerPermissible permissible) {
        return permissible.getUuid();
    }
}
