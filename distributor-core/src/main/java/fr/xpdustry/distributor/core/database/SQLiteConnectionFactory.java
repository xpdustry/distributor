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

import fr.xpdustry.distributor.core.DistributorConfiguration;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

// This code is provided to you by LuckPerms, under the MIT license.
public final class SQLiteConnectionFactory implements ConnectionFactory {

    private final DistributorConfiguration configuration;
    private final Path path;
    private final Supplier<ClassLoader> classLoaderSupplier;
    private @MonotonicNonNull Constructor<?> constructor;
    private @MonotonicNonNull NonClosableConnection connection;

    public SQLiteConnectionFactory(
            final DistributorConfiguration configuration,
            final Path path,
            final Supplier<ClassLoader> classLoaderSupplier) {
        this.configuration = configuration;
        this.path = path;
        this.classLoaderSupplier = classLoaderSupplier;
    }

    @Override
    public Connection getConnection() throws SQLException {
        NonClosableConnection connection = this.connection;
        if (connection == null || connection.isClosed()) {
            connection = new NonClosableConnection(this.createConnection());
            this.connection = connection;
        }
        return connection;
    }

    @Override
    public void start() {
        final var classLoader = this.classLoaderSupplier.get();
        try {
            final Class<?> connectionClass = classLoader.loadClass("org.sqlite.jdbc4.JDBC4Connection");
            this.constructor = connectionClass.getConstructor(String.class, String.class, Properties.class);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close0();
        }
    }

    private Connection createConnection() throws SQLException {
        try {
            return (Connection)
                    this.constructor.newInstance("jdbc:sqlite:" + this.path, this.path.toString(), new Properties());
        } catch (final ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return statement -> statement
                .replace("{prefix}", this.configuration.getDatabasePrefix())
                .replace('\'', '`');
    }
}
