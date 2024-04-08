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

import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.command.DescriptionMapper;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import com.xpdustry.distributor.plugin.PluginAware;
import revxrsal.commands.CommandHandler;

public interface MindustryCommandHandler extends CommandHandler, PluginAware {

    static MindustryCommandHandler create(
            final MindustryPlugin plugin, final DescriptionMapper<LampElement> descriptionMapper) {
        return new MindustryCommandHandlerImpl(plugin, descriptionMapper);
    }

    MindustryCommandActor wrap(final CommandSender sender);

    void initialize(final arc.util.CommandHandler handler);
}
