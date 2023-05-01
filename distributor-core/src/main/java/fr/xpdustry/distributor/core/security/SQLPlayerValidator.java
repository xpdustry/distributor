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

import com.password4j.Argon2Function;
import com.password4j.HashingFunction;
import com.password4j.types.Argon2;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent.Type;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import mindustry.gen.Groups;

public final class SQLPlayerValidator implements PlayerValidator {

    private static final HashingFunction HASH_FUNCTION = Argon2Function.getInstance(19, 2, 1, 32, Argon2.ID);

    private final ConnectionFactory factory;

    static byte[] hash(final MUUID muuid) {
        return HASH_FUNCTION.hash(muuid.getUuid(), muuid.getUsid()).getBytes();
    }

    public SQLPlayerValidator(final ConnectionFactory factory) {
        this.factory = factory;
        this.factory.executeScript(
                """
                CREATE TABLE IF NOT EXISTS muuid_validation (
                    uuid        VARBINARY(16)     NOT NULL,
                    hash        VARBINARY(32)     NOT NULL,
                    valid       BOOLEAN           NOT NULL,
                    PRIMARY KEY (uuid, hash)
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
        this.notifyChangeForOnlinePlayer(muuid, Type.VALIDATED);
    }

    @Override
    public void invalidate(final MUUID muuid) {
        this.factory.withConsumer(con -> this.saveValidation(con, new Validation(muuid, false)));
        this.notifyChangeForOnlinePlayer(muuid, Type.INVALIDATED);
    }

    @Override
    public void invalidate(final String uuid) {
        this.factory.withConsumer(con -> {
            for (final var validation : this.findValidation(con, uuid)) {
                if (validation.valid) {
                    this.saveValidation(con, new Validation(validation.uuid, validation.hash, false));
                }
            }
        });
        this.notifyChangeForOnlinePlayer(uuid, Type.INVALIDATED);
    }

    @Override
    public void invalidateAll() {
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("UPDATE muuid_validation SET valid = FALSE")) {
                statement.executeUpdate();
            }
        });
        this.notifyChangeForAllOnlinePlayers(Type.INVALIDATED);
    }

    @Override
    public void remove(final String uuid) {
        MUUID.checkUuid(uuid);
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("DELETE FROM muuid_validation WHERE uuid = ?")) {
                statement.setBytes(1, Base64.getDecoder().decode(uuid));
                statement.executeUpdate();
            }
        });
        this.notifyChangeForOnlinePlayer(uuid, Type.REMOVED);
    }

    @Override
    public void remove(final MUUID muuid) {
        this.factory.withConsumer(con -> {
            try (final var statement =
                    con.prepareStatement("DELETE FROM muuid_validation WHERE uuid = ? AND hash = ?")) {
                statement.setBytes(1, muuid.getDecodedUuid());
                statement.setBytes(2, hash(muuid));
                statement.executeUpdate();
            }
        });
        this.notifyChangeForOnlinePlayer(muuid, Type.REMOVED);
    }

    @Override
    public void removeAll() {
        this.factory.withConsumer(con -> {
            try (final var statement = con.prepareStatement("DELETE FROM muuid_validation")) {
                statement.executeUpdate();
            }
        });
        this.notifyChangeForAllOnlinePlayers(Type.REMOVED);
    }

    private Optional<Validation> findValidation(final Connection con, final MUUID muuid) throws SQLException {
        try (final var statement =
                con.prepareStatement("SELECT hash, valid FROM muuid_validation WHERE uuid = ? AND hash = ?")) {
            statement.setBytes(1, muuid.getDecodedUuid());
            statement.setBytes(2, hash(muuid));
            try (final var result = statement.executeQuery()) {
                return result.next()
                        ? Optional.of(
                                new Validation(muuid.getUuid(), result.getBytes("hash"), result.getBoolean("valid")))
                        : Optional.empty();
            }
        }
    }

    private List<Validation> findValidation(final Connection con, final String uuid) throws SQLException {
        MUUID.checkUuid(uuid);
        try (final var statement = con.prepareStatement("SELECT hash, valid FROM muuid_validation WHERE uuid = ?")) {
            statement.setBytes(1, Base64.getDecoder().decode(uuid));
            try (final var result = statement.executeQuery()) {
                final var list = new ArrayList<Validation>();
                while (result.next()) {
                    list.add(new Validation(uuid, result.getBytes("hash"), result.getBoolean("valid")));
                }
                return list;
            }
        }
    }

    private void saveValidation(final Connection con, final Validation validation) throws SQLException {
        if (this.contains(con, validation)) {
            try (final var statement =
                    con.prepareStatement("UPDATE muuid_validation SET valid = ? WHERE uuid = ? AND hash = ?")) {
                statement.setBoolean(1, validation.valid());
                statement.setBytes(2, validation.decodedUuid());
                statement.setBytes(3, validation.hash);
                statement.executeUpdate();
            }
        } else {
            try (final var statement =
                    con.prepareStatement("INSERT INTO muuid_validation (uuid, hash, valid) VALUES (?, ?, ?)")) {
                statement.setBytes(1, validation.decodedUuid());
                statement.setBytes(2, validation.hash);
                statement.setBoolean(3, validation.valid());
                statement.executeUpdate();
            }
        }
    }

    private boolean contains(final Connection con, final Validation validation) throws SQLException {
        try (final var statement = con.prepareStatement("SELECT 1 FROM muuid_validation WHERE uuid = ? AND hash = ?")) {
            statement.setBytes(1, Base64.getDecoder().decode(validation.uuid));
            statement.setBytes(2, validation.hash);
            try (final var result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private void notifyChangeForOnlinePlayer(final MUUID muuid, final PlayerValidatorEvent.Type type) {
        Groups.player.each(
                player -> player.uuid().equals(muuid.getUuid()) && player.usid().equals(muuid.getUsid()),
                player -> DistributorProvider.get().getEventBus().post(new PlayerValidatorEvent(player, type)));
    }

    private void notifyChangeForOnlinePlayer(final String uuid, final PlayerValidatorEvent.Type type) {
        Groups.player.each(
                player -> player.uuid().equals(uuid),
                player -> DistributorProvider.get().getEventBus().post(new PlayerValidatorEvent(player, type)));
    }

    private void notifyChangeForAllOnlinePlayers(final PlayerValidatorEvent.Type type) {
        Groups.player.each(
                player -> DistributorProvider.get().getEventBus().post(new PlayerValidatorEvent(player, type)));
    }

    public record Validation(String uuid, byte[] hash, boolean valid) {

        public Validation(final MUUID muuid, final boolean valid) {
            this(muuid.getUuid(), SQLPlayerValidator.hash(muuid), valid);
        }

        public byte[] decodedUuid() {
            return Base64.getDecoder().decode(this.uuid);
        }
    }
}
