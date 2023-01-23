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
package fr.xpdustry.distributor.core;

import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

public interface TestDistributorConfiguration extends DistributorConfiguration, Mutable {

    static TestDistributorConfiguration create() {
        return ConfigFactory.create(TestDistributorConfiguration.class);
    }

    default void setSchedulerWorkers(final int workers) {
        this.setProperty("distributor.scheduler.workers", String.valueOf(workers));
    }

    default void setDatabaseType(final DatabaseType type) {
        this.setProperty("distributor.database.type", type.name());
    }

    default void setDatabaseAddress(final String address) {
        this.setProperty("distributor.database.address", address);
    }

    default void setDatabasePrefix(final String prefix) {
        this.setProperty("distributor.database.prefix", prefix);
    }

    default void setDatabaseName(final String name) {
        this.setProperty("distributor.database.name", name);
    }

    default void setDatabaseUsername(final String username) {
        this.setProperty("distributor.database.username", username);
    }

    default void setDatabasePassword(final String password) {
        this.setProperty("distributor.database.password", password);
    }
}
