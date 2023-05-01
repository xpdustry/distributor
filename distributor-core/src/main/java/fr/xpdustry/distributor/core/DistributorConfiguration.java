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
package fr.xpdustry.distributor.core;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;

public interface DistributorConfiguration extends Accessible {

    @Config.Key("distributor.scheduler.workers")
    @Config.DefaultValue("-1")
    int getSchedulerWorkers();

    @Config.Key("distributor.database.type")
    @Config.DefaultValue("SQLITE")
    DatabaseType getDatabaseType();

    @Config.Key("distributor.database.address")
    @Config.DefaultValue("")
    String getDatabaseAddress();

    @Config.Key("distributor.database.prefix")
    @Config.DefaultValue("")
    String getDatabasePrefix();

    @Config.Key("distributor.database.name")
    @Config.DefaultValue("distributor")
    String getDatabaseName();

    @Config.Key("distributor.database.username")
    @Config.DefaultValue("")
    String getDatabaseUsername();

    @Config.Key("distributor.database.password")
    @Config.DefaultValue("")
    String getDatabasePassword();

    @Config.Key("distributor.database.pool.min")
    @Config.DefaultValue("1")
    int getDatabaseMinPoolSize();

    @Config.Key("distributor.database.pool.max")
    @Config.DefaultValue("4")
    int getDatabaseMaxPoolSize();

    @Config.Key("distributor.security.validation.policy")
    @Config.DefaultValue("VALIDATE_UNKNOWN")
    PlayerValidationPolicy getIdentityValidationPolicy();

    @Config.Key("distributor.security.validation.auto-admin")
    @Config.DefaultValue("false")
    boolean isValidationAutoAdminEnabled();

    @Config.Key("distributor.security.permission.primary-group")
    @Config.DefaultValue("default")
    String getPermissionPrimaryGroup();

    @Config.Key("distributor.security.permission.ignore-admin")
    @Config.DefaultValue("false")
    boolean isAdminIgnored();

    enum DatabaseType {
        SQLITE,
        MYSQL
    }

    enum PlayerValidationPolicy {
        VALIDATE_UNKNOWN,
        VALIDATE_ALL,
        VALIDATE_NONE
    }
}
