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
import fr.xpdustry.distributor.core.DistributorConfiguration;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;

// This code is provided to you by LuckPerms, under the MIT license.
public final class MySQLConnectionFactory extends HikariConnectionFactory {

    public MySQLConnectionFactory(final DistributorConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return super.getStatementProcessor().andThen(statement -> statement.replace('\'', '`'));
    }

    @Override
    public void start() {
        super.start();

        // Calling Class.forName("com.mysql.cj.jdbc.Driver") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been set up.
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (final SQLException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    protected int getDefaultPort() {
        return 3306;
    }

    @Override
    protected void configure(
            final HikariConfig config,
            final String host,
            final int port,
            final String database,
            final String username,
            final String password) {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    protected Map<String, String> getExtraProperties() {
        final var properties = super.getExtraProperties();

        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");

        // https://stackoverflow.com/a/54256150
        properties.putIfAbsent("serverTimezone", "UTC");

        return properties;
    }
}
