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
package com.xpdustry.distributor.command;

import arc.util.CommandHandler;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import org.jspecify.annotations.Nullable;

public interface CommandFacade {

    static CommandFacade wrap(final CommandHandler.Command command) {
        if (command instanceof CommandFacade facade) {
            return facade;
        } else {
            return new ArcCommandFacade(command);
        }
    }

    String getRealName();

    String getName();

    DescriptionFacade getDescription();

    boolean isAlias();

    boolean isPrefixed();

    boolean isVisible(final CommandSender sender);

    CommandHelp getHelp(final CommandSender sender, final String query);

    @Nullable MindustryPlugin getPlugin();
}