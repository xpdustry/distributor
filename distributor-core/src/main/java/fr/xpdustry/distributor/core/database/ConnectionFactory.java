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

import java.sql.Connection;
import java.sql.SQLException;
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
}
