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

import fr.xpdustry.distributor.api.permission.PermissibleManager;
import fr.xpdustry.distributor.api.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SQLPlayerPermissibleManager implements PermissibleManager<PlayerPermissible> {

    private static final String PLAYER_INSERT = "INSERT INTO '{prefix}player' ('uuid', 'parents') VALUES (?, ?)";
    private static final String PLAYER_UPDATE = "UPDATE '{prefix}player' SET 'parents' = ? WHERE 'uuid' = ?";
    private static final String PLAYER_SELECT = "SELECT 'uuid', 'parents' FROM '{prefix}player' WHERE 'uuid' = ?";
    private static final String PLAYER_DELETE = "DELETE FROM '{prefix}player' WHERE 'uuid' = ?";
    private static final String PLAYER_DELETE_ALL = "DELETE FROM '{prefix}player'";
    private static final String PLAYER_SELECT_ALL = "SELECT 'uuid', 'parents' FROM '{prefix}player'";
    private static final String PLAYER_COUNT = "SELECT COUNT(*) FROM '{prefix}player'";

    private static final String PLAYER_PERMISSION_INSERT =
            "INSERT INTO '{prefix}player_permission' ('player_uuid', 'permission', 'value') VALUES (?, ?, ?)";
    private static final String PLAYER_PERMISSION_SELECT =
            "SELECT 'permission', 'value' FROM '{prefix}player_permission' WHERE 'player_uuid' = ?";
    private static final String PLAYER_PERMISSION_DELETE_ALL =
            "DELETE FROM '{prefix}player_permission' WHERE 'player_uuid' = ?";

    private final ConnectionFactory factory;

    public SQLPlayerPermissibleManager(final ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void save(final PlayerPermissible permissible) {
        this.factory.withConsumer(con -> {
            if (this.exists(con, permissible.getUuid())) {
                this.update(con, permissible);
            } else {
                this.insert(con, permissible);
            }
        });
    }

    private void update(final Connection con, final PlayerPermissible permissible) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_UPDATE))) {
            statement.setString(1, String.join(";", permissible.getParentGroups()));
            statement.setString(2, permissible.getUuid());
            statement.executeUpdate();
        }
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_PERMISSION_DELETE_ALL))) {
            statement.setString(1, permissible.getUuid());
            statement.executeUpdate();
        }
        this.insertPermissions(con, permissible);
    }

    private void insert(final Connection con, final PlayerPermissible permissible) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_INSERT))) {
            statement.setString(1, permissible.getUuid());
            statement.setString(2, String.join(";", permissible.getParentGroups()));
            statement.executeUpdate();
        }
        this.insertPermissions(con, permissible);
    }

    private void insertPermissions(final Connection con, final PlayerPermissible permissible) throws SQLException {
        for (final var permission : permissible.getPermissions().entrySet()) {
            try (final var statement =
                    con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_PERMISSION_INSERT))) {
                statement.setString(1, permissible.getUuid());
                statement.setString(2, permission.getKey());
                statement.setBoolean(3, permission.getValue());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public PlayerPermissible findOrCreateById(final String id) {
        return this.factory.withFunction(con -> this.find(con, id).orElseGet(() -> new SimplePlayerPermissible(id)));
    }

    @Override
    public Optional<PlayerPermissible> findById(final String id) {
        return this.factory.withFunction(con -> this.find(con, id));
    }

    private Optional<PlayerPermissible> find(final Connection con, final String uuid) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_SELECT))) {
            statement.setString(1, uuid);
            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(this.create(con, result));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<PlayerPermissible> findAll() {
        return this.factory.withFunction(this::findAll);
    }

    private List<PlayerPermissible> findAll(final Connection con) throws SQLException {
        final var list = new ArrayList<PlayerPermissible>();
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_SELECT_ALL))) {
            try (final var result = statement.executeQuery()) {
                while (result.next()) {
                    list.add(this.create(con, result));
                }
            }
        }
        return list;
    }

    private PlayerPermissible create(final Connection con, final ResultSet result) throws SQLException {
        final var player = new SimplePlayerPermissible(result.getString("uuid"));
        final var parents = result.getString("parents");
        if (!parents.isBlank()) {
            player.setParents(List.of(parents.split(";")));
        }
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_PERMISSION_SELECT))) {
            statement.setString(1, player.getUuid());
            try (final var permissions = statement.executeQuery()) {
                while (permissions.next()) {
                    player.setPermission(
                            permissions.getString("permission"), Tristate.of(permissions.getBoolean("value")));
                }
            }
        }
        return player;
    }

    @Override
    public boolean exists(final PlayerPermissible permissible) {
        return this.factory.withFunction(con -> this.exists(con, permissible.getUuid()));
    }

    private boolean exists(final Connection con, final String uuid) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_SELECT))) {
            statement.setString(1, uuid);
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
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_COUNT))) {
            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getLong(1);
                }
            }
        }
        throw new SQLException("Could not count players");
    }

    @Override
    public void deleteById(final String id) {
        this.factory.withConsumer(con -> this.delete(con, id));
    }

    @Override
    public void delete(final PlayerPermissible permissible) {
        this.factory.withConsumer(con -> this.delete(con, permissible.getUuid()));
    }

    private void delete(final Connection con, final String uuid) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_DELETE))) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAll() {
        this.factory.withConsumer(this::deleteAll);
    }

    private void deleteAll(final Connection con) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(PLAYER_DELETE_ALL))) {
            statement.executeUpdate();
        }
    }
}
