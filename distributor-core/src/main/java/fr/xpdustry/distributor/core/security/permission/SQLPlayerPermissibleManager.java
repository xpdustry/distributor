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

import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLPlayerPermissibleManager extends AbstractSQLPermissibleManager<PlayerPermissible> {

    public SQLPlayerPermissibleManager(final ConnectionFactory factory) {
        super(factory, "player", "uuid");
    }

    @Override
    protected void update(final Connection con, final PlayerPermissible permissible) {}

    @Override
    protected void insert(final Connection con, final PlayerPermissible permissible) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.processStatement("INSERT INTO '{prefix}player' ('uuid') VALUES (?)"))) {
            statement.setString(1, permissible.getUuid());
            statement.executeUpdate();
        }
    }

    @Override
    protected String getIdentifier(final PlayerPermissible permissible) {
        return permissible.getUuid();
    }

    @Override
    protected PlayerPermissible createPermissible(final String identifier) {
        return new SimplePlayerPermissible(identifier);
    }

    @Override
    protected PlayerPermissible createPermissible(final ResultSet result) throws SQLException {
        return new SimplePlayerPermissible(result.getString("uuid"));
    }
}
