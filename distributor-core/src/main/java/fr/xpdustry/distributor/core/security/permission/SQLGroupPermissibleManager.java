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
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SQLGroupPermissibleManager implements PermissibleManager<GroupPermissible> {

    private static final String GROUP_INSERT =
            "INSERT INTO '{prefix}group' ('name', 'parents', 'weight') VALUES (?, ?, ?)";
    private static final String GROUP_UPDATE =
            "UPDATE '{prefix}group' SET 'parents' = ?, 'weight' = ? WHERE 'name' = ?";
    private static final String GROUP_SELECT =
            "SELECT 'name', 'parents', 'weight' FROM '{prefix}group' WHERE 'name' = ?";
    private static final String GROUP_DELETE = "DELETE FROM '{prefix}group' WHERE 'name' = ?";
    private static final String GROUP_DELETE_ALL = "DELETE FROM '{prefix}group'";
    private static final String GROUP_SELECT_ALL = "SELECT 'name', 'parents', 'weight' FROM '{prefix}group'";
    private static final String GROUP_COUNT = "SELECT COUNT(*) FROM '{prefix}group'";

    private static final String GROUP_PERMISSION_INSERT =
            "INSERT INTO '{prefix}group_permission' ('group_name', 'permission', 'value') VALUES (?, ?, ?)";
    private static final String GROUP_PERMISSION_SELECT =
            "SELECT 'permission', 'value' FROM '{prefix}group_permission' WHERE 'group_name' = ?";
    private static final String GROUP_PERMISSION_DELETE_ALL =
            "DELETE FROM '{prefix}group_permission' WHERE 'group_name' = ?";

    private final ConnectionFactory factory;

    public SQLGroupPermissibleManager(final ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void save(final GroupPermissible permissible) {
        this.factory.withConsumer(con -> {
            if (this.exists(con, permissible.getName())) {
                this.update(con, permissible);
            } else {
                this.insert(con, permissible);
            }
        });
    }

    private void update(final Connection con, final GroupPermissible permissible) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_UPDATE))) {
            statement.setString(1, String.join(";", permissible.getParentGroups()));
            statement.setString(2, permissible.getName());
            statement.setInt(3, permissible.getWeight());
            statement.executeUpdate();
        }
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_PERMISSION_DELETE_ALL))) {
            statement.setString(1, permissible.getName());
            statement.executeUpdate();
        }
        this.insertPermissions(con, permissible);
    }

    private void insert(final Connection con, final GroupPermissible permissible) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_INSERT))) {
            statement.setString(1, permissible.getName());
            statement.setString(2, String.join(";", permissible.getParentGroups()));
            statement.setInt(3, permissible.getWeight());
            statement.executeUpdate();
        }
        this.insertPermissions(con, permissible);
    }

    private void insertPermissions(final Connection con, final GroupPermissible permissible) throws SQLException {
        for (final var permission : permissible.getPermissions().entrySet()) {
            try (final var statement =
                    con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_PERMISSION_INSERT))) {
                statement.setString(1, permissible.getName());
                statement.setString(2, permission.getKey());
                statement.setBoolean(3, permission.getValue());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public GroupPermissible findOrCreateById(final String id) {
        return this.factory.withFunction(con -> this.find(con, id).orElseGet(() -> new SimpleGroupPermissible(id)));
    }

    @Override
    public Optional<GroupPermissible> findById(final String id) {
        return this.factory.withFunction(con -> this.find(con, id));
    }

    private Optional<GroupPermissible> find(final Connection con, final String name) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_SELECT))) {
            statement.setString(1, name);
            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(this.create(con, result));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<GroupPermissible> findAll() {
        return this.factory.withFunction(this::findAll);
    }

    private List<GroupPermissible> findAll(final Connection con) throws SQLException {
        final var list = new ArrayList<GroupPermissible>();
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_SELECT_ALL))) {
            try (final var result = statement.executeQuery()) {
                while (result.next()) {
                    list.add(this.create(con, result));
                }
            }
        }
        return list;
    }

    private GroupPermissible create(final Connection con, final ResultSet result) throws SQLException {
        final var group = new SimpleGroupPermissible(result.getString("name"));
        final var parents = result.getString("parents");
        if (!parents.isBlank()) {
            group.setParents(List.of(parents.split(";")));
        }
        group.setWeight(result.getInt("weight"));
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_PERMISSION_SELECT))) {
            statement.setString(1, group.getName());
            try (final var permissions = statement.executeQuery()) {
                while (permissions.next()) {
                    group.setPermission(
                            permissions.getString("permission"), Tristate.of(permissions.getBoolean("value")));
                }
            }
        }
        return group;
    }

    @Override
    public boolean exists(final GroupPermissible permissible) {
        return this.factory.withFunction(con -> this.exists(con, permissible.getName()));
    }

    private boolean exists(final Connection con, final String name) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_SELECT))) {
            statement.setString(1, name);
            try (final var result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    @Override
    public long count() {
        return this.factory.withFunction(this::count);
    }

    private long count(final Connection con) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_COUNT))) {
            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getLong(1);
                }
            }
        }
        throw new SQLException("Could not count groups");
    }

    @Override
    public void deleteById(final String id) {
        this.factory.withConsumer(con -> this.delete(con, id));
    }

    @Override
    public void delete(final GroupPermissible permissible) {
        this.factory.withConsumer(con -> this.delete(con, permissible.getName()));
    }

    private void delete(final Connection con, final String name) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_DELETE))) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAll() {
        this.factory.withConsumer(this::deleteAll);
    }

    private void deleteAll(final Connection con) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(GROUP_DELETE_ALL))) {
            statement.executeUpdate();
        }
    }
}
