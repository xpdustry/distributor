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
package com.xpdustry.distributor.api.command.lamp;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.api.command.lamp.actor.ActorFactory;
import com.xpdustry.distributor.api.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.api.command.lamp.description.LampDescription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import revxrsal.commands.Lamp;

import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.componentResponseHandler;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.mindustryExceptionHandler;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.mindustryParameterTypes;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.mindustryParameterValidators;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.mindustryPermissions;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.mindustrySenderResolver;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.pluginContextParameters;
import static com.xpdustry.distributor.api.command.lamp.MindustryVisitors.registrationHooks;

public final class MindustryLamp {

    public static <A extends MindustryCommandActor> Lamp.Builder<A> builder(
            final MindustryPlugin plugin, final CommandHandler handler) {
        return Lamp.<A>builder()
                .accept(mindustryExceptionHandler())
                .accept(mindustryParameterTypes())
                .accept(mindustryParameterValidators())
                .accept(mindustrySenderResolver())
                .accept(mindustryPermissions())
                .accept(registrationHooks(
                        plugin,
                        handler,
                        ActorFactory.simple(),
                        DescriptionMapper.text(LampDescription::getDescription)))
                .accept(pluginContextParameters(plugin))
                .accept(componentResponseHandler());
    }
}
