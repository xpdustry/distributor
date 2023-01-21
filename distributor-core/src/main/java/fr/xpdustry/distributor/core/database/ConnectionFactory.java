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
package fr.xpdustry.distributor.core.database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// This code is provided to you by LuckPerms, under the MIT license.
public interface ConnectionFactory extends AutoCloseable {

    Connection getConnection() throws SQLException;

    default void withConsumer(final ConnectionConsumer consumer) {
        try (final var con = this.getConnection()) {
            consumer.accept(con);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    default <T> T withFunction(final ConnectionFunction<T> function) {
        try (final var con = this.getConnection()) {
            return function.apply(con);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Function<String, String> getStatementProcessor();

    void start();

    @Override
    void close() throws SQLException;

    default void executeScript(final InputStream stream) {
        try (final var con = this.getConnection();
                final var statements = con.createStatement()) {
            for (final var statement : this.readStatements(stream)) {
                statements.addBatch(this.getStatementProcessor().apply(statement));
            }
            statements.executeBatch();
        } catch (final SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void executeScript(final String script) {
        this.executeScript(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
    }

    private List<String> readStatements(final InputStream stream) throws IOException {
        final List<String> statements = new ArrayList<>();

        try (final var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            var builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                builder.append(line);

                // check for end of declaration
                if (line.endsWith(";")) {
                    builder.deleteCharAt(builder.length() - 1);

                    final String result = builder.toString().trim();
                    if (!result.isEmpty()) {
                        statements.add(result);
                    }

                    // reset
                    builder = new StringBuilder();
                }
            }
        }

        return statements;
    }
}
