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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.xpdustry.distributor.core.DistributorConfiguration;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

// This code is provided to you by LuckPerms, under the MIT license.
public abstract class HikariConnectionFactory implements ConnectionFactory {

    private final DistributorConfiguration configuration;
    private @MonotonicNonNull HikariDataSource dataSource = null;

    public HikariConnectionFactory(final DistributorConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }
        final var connection = this.dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }
        return connection;
    }

    @Override
    public void start() {
        final var config = new HikariConfig();

        final var parts = this.configuration.getDatabaseAddress().split(":", 2);
        final var host = parts[0];
        final var port = parts.length == 1 ? this.getDefaultPort() : Integer.parseInt(parts[1]);
        this.configure(
                config,
                host,
                port,
                this.configuration.getDatabaseName(),
                this.configuration.getDatabaseUsername(),
                this.configuration.getDatabasePassword());

        config.setPoolName("distributor-pool");
        config.setMinimumIdle(this.configuration.getDatabaseMinPoolSize());
        config.setMaximumPoolSize(this.configuration.getDatabaseMaxPoolSize());
        this.getExtraProperties().forEach(config::addDataSourceProperty);
        // No need to initialize the pool now, the database is always initialized after this method is called.
        config.setInitializationFailTimeout(-1);
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    protected abstract int getDefaultPort();

    protected abstract void configure(
            final HikariConfig hikari, String host, int port, String database, String username, String password);

    protected Map<String, String> getExtraProperties() {
        return new HashMap<>();
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return statement -> statement.replace("{prefix}", this.configuration.getDatabasePrefix());
    }
}
