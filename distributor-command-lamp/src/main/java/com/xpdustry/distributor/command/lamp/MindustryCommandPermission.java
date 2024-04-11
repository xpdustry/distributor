/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.command.lamp;

import com.xpdustry.distributor.internal.DistributorDataClass;
import org.immutables.value.Value;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.CommandPermission;

@DistributorDataClass
@Value.Immutable
public sealed interface MindustryCommandPermission extends CommandPermission permits MindustryCommandPermissionImpl {

    static MindustryCommandPermission of(final String permission) {
        return MindustryCommandPermissionImpl.of(permission);
    }

    String getPermission();

    @Override
    default boolean canExecute(final CommandActor actor) {
        return ((MindustryCommandActor) actor)
                .getCommandSender()
                .hasPermission(getPermission())
                .asBoolean();
    }
}
