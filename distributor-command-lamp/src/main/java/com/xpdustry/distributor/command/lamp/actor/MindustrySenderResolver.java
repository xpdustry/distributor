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
package com.xpdustry.distributor.command.lamp.actor;

import com.xpdustry.distributor.api.command.CommandSender;
import mindustry.gen.Player;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.process.SenderResolver;

public enum MindustrySenderResolver implements SenderResolver<MindustryCommandActor> {
    INSTANCE;

    @Override
    public boolean isSenderType(final CommandParameter parameter) {
        return Player.class.isAssignableFrom(parameter.type())
                || CommandSender.class.isAssignableFrom(parameter.type());
    }

    @Override
    public Object getSender(
            final Class<?> customSenderType,
            final MindustryCommandActor actor,
            final ExecutableCommand<MindustryCommandActor> command) {
        if (Player.class.isAssignableFrom(customSenderType)) {
            return actor.getCommandSender().getPlayer();
        }
        return actor.getCommandSender();
    }
}
