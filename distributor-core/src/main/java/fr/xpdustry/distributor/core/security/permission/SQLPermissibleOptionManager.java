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

import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class SQLPermissibleOptionManager {

    private static final String OPTION_INSERT =
            "INSERT INTO '{prefix}permission_option' ('key', 'value', 'type') VALUES (?, ?, ?)";
    private static final String OPTION_UPDATE =
            "UPDATE '{prefix}permission_option' SET 'value' = ?, 'type' = ? WHERE 'key' = ?";
    private static final String OPTION_SELECT =
            "SELECT 'value', 'type' FROM '{prefix}permission_option' WHERE 'key' = ?";
    private static final String OPTION_DELETE = "DELETE FROM '{prefix}permission_option' WHERE 'key' = ?";

    private final Map<Integer, Codec<?>> codecs = Map.of(
            0, new Codec<>(String.class, Function.identity(), Function.identity()),
            1, new Codec<>(Integer.class, Object::toString, Integer::parseInt),
            2, new Codec<>(Boolean.class, Object::toString, Boolean::parseBoolean));

    private final ConnectionFactory factory;

    public SQLPermissibleOptionManager(final ConnectionFactory factory) {
        this.factory = factory;
    }

    public <V> void set(final String key, final V value) {
        this.factory.withConsumer(con -> {
            if (this.exists(con, key)) {
                this.update(con, key, value);
            } else {
                this.insert(con, key, value);
            }
        });
    }

    public boolean contains(final String key) {
        return this.factory.withFunction(con -> this.exists(con, key));
    }

    private boolean exists(final Connection con, final String key) throws SQLException {
        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(OPTION_SELECT))) {
            statement.setString(1, key);
            try (final var result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void insert(final Connection con, final String key, final V value) throws SQLException {
        final var entry = this.codecs.entrySet().stream()
                .filter(t -> t.getValue().clazz.equals(value.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + value.getClass()));
        final var codec = (Codec<V>) entry.getValue();

        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(OPTION_INSERT))) {
            statement.setString(1, key);
            statement.setString(2, codec.encoder.apply(value));
            statement.setInt(3, entry.getKey());
            statement.executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void update(final Connection con, final String key, final V value) throws SQLException {
        final var entry = this.codecs.entrySet().stream()
                .filter(t -> t.getValue().clazz.equals(value.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + value.getClass()));
        final var codec = (Codec<V>) entry.getValue();

        try (final var statement =
                con.prepareStatement(this.factory.getStatementProcessor().apply(OPTION_UPDATE))) {
            statement.setString(1, codec.encoder.apply(value));
            statement.setInt(2, entry.getKey());
            statement.setString(3, key);
            statement.executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Optional<V> get(final String key, final Class<V> clazz) {
        return this.factory.withFunction(con -> {
            final var entry = this.codecs.entrySet().stream()
                    .filter(t -> t.getValue().clazz.equals(clazz))
                    .findFirst();
            if (entry.isEmpty()) {
                throw new IllegalArgumentException("Unsupported type: " + clazz);
            }

            final var codec = (Codec<V>) entry.get().getValue();
            try (final var statement =
                    con.prepareStatement(this.factory.getStatementProcessor().apply(OPTION_SELECT))) {
                statement.setString(1, key);
                try (final var result = statement.executeQuery()) {
                    if (result.next()) {
                        final var value = result.getString(1);
                        final var type = result.getInt(2);
                        if (type != entry.get().getKey()) {
                            throw new IllegalStateException("Type mismatch: " + type + " != "
                                    + entry.get().getKey());
                        }
                        return Optional.of(codec.decoder.apply(value));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        });
    }

    public void remove(final String key) {
        this.factory.withConsumer(con -> {
            try (final var statement =
                    con.prepareStatement(this.factory.getStatementProcessor().apply(OPTION_DELETE))) {
                statement.setString(1, key);
                statement.executeUpdate();
            }
        });
    }

    private record Codec<V>(Class<V> clazz, Function<V, String> encoder, Function<String, V> decoder) {}
}
