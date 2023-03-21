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

import fr.xpdustry.distributor.api.security.permission.Permissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractSQLPermissibleManager<P extends Permissible> implements PermissibleManager<P> {

    private final ConnectionFactory factory;
    private final Function<String, String> statementProcessor;

    public AbstractSQLPermissibleManager(
            final ConnectionFactory factory, final String category, final String primaryKey) {
        this.factory = factory;
        this.statementProcessor = this.factory.getStatementProcessor().andThen(s -> s.replace("{pm}", category)
                .replace("{pk}", primaryKey));
    }

    @Override
    public void save(final P permissible) {
        this.factory.withConsumer(con -> {
            if (this.exists(permissible)) {
                this.update(con, permissible);
            } else {
                this.insert(con, permissible);
            }
            this.updateParentsAndPermissions(con, permissible);
        });
    }

    protected abstract void update(final Connection con, final P permissible) throws SQLException;

    protected abstract void insert(final Connection con, final P permissible) throws SQLException;

    @Override
    public P findOrCreateById(final String id) {
        return this.findById(id).orElseGet(() -> this.createPermissible(id));
    }

    @Override
    public Optional<P> findById(final String id) {
        return this.factory.withFunction(con -> {
            try (final var statement =
                    con.prepareStatement(this.processStatement("SELECT * FROM '{prefix}{pm}' WHERE {pk} = ?"))) {
                statement.setString(1, id);
                try (final var result = statement.executeQuery()) {
                    if (result.next()) {
                        final var permissible = this.createPermissible(result);
                        this.selectParentAndPermissions(con, permissible);
                        return Optional.of(permissible);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public Iterable<P> findAll() {
        return this.factory.withFunction(con -> {
            final var list = new ArrayList<P>();
            try (final var statement = con.prepareStatement(this.processStatement("SELECT * FROM '{prefix}{pm}'"))) {
                try (final var result = statement.executeQuery()) {
                    while (result.next()) {
                        final var permissible = this.createPermissible(result);
                        this.selectParentAndPermissions(con, permissible);
                        list.add(permissible);
                    }
                }
            }
            return list;
        });
    }

    @Override
    public boolean exists(final P permissible) {
        return this.factory.withFunction(con -> {
            try (final var statement =
                    con.prepareStatement(this.processStatement("SELECT 1 FROM '{prefix}{pm}' WHERE {pk} = ?"))) {
                statement.setString(1, this.getIdentifier(permissible));
                try (final var result = statement.executeQuery()) {
                    return result.next();
                }
            }
        });
    }

    @Override
    public long count() {
        return this.factory.withFunction(con -> {
            try (final var statement =
                    con.prepareStatement(this.processStatement("SELECT COUNT(*) FROM '{prefix}{pm}'"))) {
                try (final var result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getLong(1);
                    }
                }
            }
            throw new SQLException("Could not count permissibles");
        });
    }

    @Override
    public void deleteById(final String id) {
        this.factory.withConsumer(con -> {
            try (final var statement =
                    con.prepareStatement(this.processStatement("DELETE FROM '{prefix}{pm}' WHERE {pk} = ?"))) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
        });
    }

    @Override
    public void delete(final P permissible) {
        this.deleteById(this.getIdentifier(permissible));
    }

    @Override
    public void deleteAll() {
        this.factory.withConsumer(con -> {
            try (final var statement =
                    con.prepareStatement(this.factory.getStatementProcessor().apply("DELETE FROM '{prefix}{pm}'"))) {
                statement.executeUpdate();
            }
        });
    }

    protected abstract String getIdentifier(final P permissible);

    protected abstract P createPermissible(final String identifier);

    protected abstract P createPermissible(final ResultSet result) throws SQLException;

    protected String processStatement(final String statement) {
        return this.statementProcessor.apply(statement);
    }

    private void updateParentsAndPermissions(final Connection con, final P permissible) throws SQLException {
        try (final var statement = con.prepareStatement(
                this.processStatement("DELETE FROM '{prefix}{pm}_parent_group' WHERE {pm}_{pk} = ?"))) {
            statement.setString(1, this.getIdentifier(permissible));
            statement.executeUpdate();
        }
        if (!permissible.getParentGroups().isEmpty()) {
            try (final var statement = con.prepareStatement(this.processStatement(
                    "INSERT INTO '{prefix}{pm}_parent_group' ('{pm}_{pk}', 'parent_group') VALUES (?, ?)"))) {
                for (final var parent : permissible.getParentGroups()) {
                    statement.setString(1, this.getIdentifier(permissible));
                    statement.setString(2, parent);
                    statement.addBatch();
                    statement.clearParameters();
                }
                statement.executeBatch();
            }
        }
        try (final var statement = con.prepareStatement(
                this.processStatement("DELETE FROM '{prefix}{pm}_permission' WHERE '{pm}_{pk}' = ?"))) {
            statement.setString(1, this.getIdentifier(permissible));
            statement.executeUpdate();
        }
        if (!permissible.getPermissions().isEmpty()) {
            try (final var statement = con.prepareStatement(this.processStatement(
                    "INSERT INTO '{prefix}{pm}_permission' ('{pm}_{pk}', 'permission', 'value') VALUES (?, ?, ?)"))) {
                for (final var permission : permissible.getPermissions().entrySet()) {
                    statement.setString(1, this.getIdentifier(permissible));
                    statement.setString(2, permission.getKey());
                    statement.setBoolean(3, permission.getValue());
                    statement.addBatch();
                    statement.clearParameters();
                }
                statement.executeBatch();
            }
        }
    }

    private void selectParentAndPermissions(final Connection con, final P permissible) throws SQLException {
        try (final var statement = con.prepareStatement(
                this.processStatement("SELECT 'parent_group' FROM '{prefix}{pm}_parent_group' WHERE {pm}_{pk} = ?"))) {
            statement.setString(1, this.getIdentifier(permissible));
            try (final var result = statement.executeQuery()) {
                while (result.next()) {
                    permissible.addParentGroup(result.getString(1));
                }
            }
        }
        try (final var statement = con.prepareStatement(this.processStatement(
                "SELECT 'permission', 'value' FROM '{prefix}{pm}_permission' WHERE {pm}_{pk} = ?"))) {
            statement.setString(1, this.getIdentifier(permissible));
            try (final var result = statement.executeQuery()) {
                while (result.next()) {
                    permissible.setPermission(result.getString(1), Tristate.of(result.getBoolean(2)));
                }
            }
        }
    }
}
