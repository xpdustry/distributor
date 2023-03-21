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
package fr.xpdustry.distributor.core.security;

import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SQLPlayerValidator implements PlayerValidator {

    private final ConnectionFactory factory;

    public SQLPlayerValidator(final ConnectionFactory factory) {
        this.factory = factory;
        this.factory.executeScript(
                """
                CREATE TABLE IF NOT EXISTS validated_muuid (
                    uuid    VARCHAR(24)     NOT NULL,
                    usid    VARCHAR(12)     NOT NULL,
                    valid   BOOLEAN         NOT NULL    DEFAULT TRUE,
                    PRIMARY KEY (uuid, usid)
                );
                """);
    }

    @Override
    public boolean isValid(final MUUID muuid) {
        return this.factory.withFunction(
                con -> this.findValidation(con, muuid).map(Validation::valid).orElse(false));
    }

    @Override
    public boolean contains(final String uuid) {
        return this.factory.withFunction(con -> !this.findValidation(con, uuid).isEmpty());
    }

    @Override
    public boolean contains(final MUUID muuid) {
        return this.factory.withFunction(con -> this.findValidation(con, muuid).isPresent());
    }

    @Override
    public void validate(final MUUID muuid) {
        this.factory.withConsumer(con -> this.saveValidation(con, new Validation(muuid, true)));
    }

    @Override
    public void invalidate(final MUUID muuid) {
        this.factory.withConsumer(con -> this.saveValidation(con, new Validation(muuid, false)));
    }

    @Override
    public void invalidate(final String uuid) {
        this.factory.withConsumer(con -> {
            for (final var validation : this.findValidation(con, uuid)) {
                if (validation.valid) {
                    this.saveValidation(con, new Validation(validation.muuid, false));
                }
            }
        });
    }

    @Override
    public void invalidateAll() {
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("UPDATE validated_muuid SET valid = FALSE")) {
                statement.executeUpdate();
            }
        });
    }

    @Override
    public void remove(final String uuid) {
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("DELETE FROM validated_muuid WHERE uuid = ?")) {
                statement.setString(1, uuid);
                statement.executeUpdate();
            }
        });
    }

    @Override
    public void remove(final MUUID muuid) {
        this.factory.withConsumer(con -> {
            try (final var statement =
                    con.prepareStatement("DELETE FROM validated_muuid WHERE uuid = ? AND usid = ?")) {
                statement.setString(1, muuid.getUuid());
                statement.setString(2, muuid.getUsid());
                statement.executeUpdate();
            }
        });
    }

    @Override
    public void removeAll() {
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("DELETE FROM validated_muuid")) {
                statement.executeUpdate();
            }
        });
    }

    private Optional<Validation> findValidation(final Connection con, final MUUID muuid) throws SQLException {
        try (final var statement =
                con.prepareStatement("SELECT valid FROM validated_muuid WHERE uuid = ? AND usid = ?")) {
            statement.setString(1, muuid.getUuid());
            statement.setString(2, muuid.getUsid());
            try (final var result = statement.executeQuery()) {
                return result.next()
                        ? Optional.of(new Validation(muuid, result.getBoolean("valid")))
                        : Optional.empty();
            }
        }
    }

    private List<Validation> findValidation(final Connection con, final String uuid) throws SQLException {
        try (final var statement = con.prepareStatement("SELECT usid, valid FROM validated_muuid WHERE uuid = ?")) {
            statement.setString(1, uuid);
            try (final var result = statement.executeQuery()) {
                final var list = new ArrayList<Validation>();
                while (result.next()) {
                    list.add(new Validation(uuid, result.getString("usid"), result.getBoolean("valid")));
                }
                return list;
            }
        }
    }

    private void saveValidation(final Connection con, final Validation validation) throws SQLException {
        if (this.findValidation(con, validation.muuid()).isPresent()) {
            try (final var statement =
                    con.prepareStatement("UPDATE validated_muuid SET valid = ? WHERE uuid = ? AND usid = ?")) {
                statement.setBoolean(1, validation.valid());
                statement.setString(2, validation.muuid().getUuid());
                statement.setString(3, validation.muuid().getUsid());
                statement.executeUpdate();
            }
        } else {
            try (final var statement =
                    con.prepareStatement("INSERT INTO validated_muuid (uuid, usid, valid) VALUES (?, ?, ?)")) {
                statement.setString(1, validation.muuid().getUuid());
                statement.setString(2, validation.muuid().getUsid());
                statement.setBoolean(3, validation.valid());
                statement.executeUpdate();
            }
        }
    }

    public record Validation(MUUID muuid, boolean valid) {
        public Validation(final String uuid, final String usid, final boolean valid) {
            this(MUUID.of(uuid, usid), valid);
        }
    }
}
