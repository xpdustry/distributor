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

import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLGroupPermissibleManager extends AbstractSQLPermissibleManager<GroupPermissible> {

    public SQLGroupPermissibleManager(final ConnectionFactory factory) {
        super(factory, "group", "name");
    }

    @Override
    protected void update(final Connection con, final GroupPermissible permissible) throws SQLException {
        try (final var statement = con.prepareStatement(
                this.processStatement("UPDATE '{prefix}group' SET 'weight' = ? WHERE 'name' = ?"))) {
            statement.setInt(1, permissible.getWeight());
            statement.setString(2, permissible.getName());
            statement.executeUpdate();
        }
    }

    @Override
    protected void insert(final Connection con, final GroupPermissible permissible) throws SQLException {
        try (final var statement = con.prepareStatement(
                this.processStatement("INSERT INTO '{prefix}group' ('name', 'weight') VALUES (?, ?)"))) {
            statement.setString(1, permissible.getName());
            statement.setInt(2, permissible.getWeight());
            statement.executeUpdate();
        }
    }

    @Override
    protected String getIdentifier(final GroupPermissible permissible) {
        return permissible.getName();
    }

    @Override
    protected GroupPermissible createPermissible(final String identifier) {
        return new SimpleGroupPermissible(identifier);
    }

    @Override
    protected GroupPermissible createPermissible(final ResultSet result) throws SQLException {
        final var group = new SimpleGroupPermissible(result.getString("name"));
        group.setWeight(result.getInt("weight"));
        return group;
    }
}
