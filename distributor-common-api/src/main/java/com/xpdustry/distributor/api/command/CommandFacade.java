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
package com.xpdustry.distributor.api.command;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@code CommandFacade} is a simple abstraction for advanced command systems in Mindustry.
 * It is typically implemented by a {@link CommandHandler.Command}.
 */
public interface CommandFacade {

    /**
     * Extracts a {@code CommandFacade} from a native command.
     *
     * @param command the command to extract from
     * @return the facade of the native command
     */
    static CommandFacade from(final CommandHandler.Command command) {
        if (command instanceof CommandFacade facade) {
            return facade;
        } else {
            return new MindustryCommandFacade(command);
        }
    }

    /**
     * Returns the real name of this command.
     */
    String getRealName();

    /**
     * Returns the name of this command.
     * Usually returns {@link #getRealName()}, but if another command with the same name was registered before,
     * the owning plugin identifier will be prefixed to the name. Such as {@code plugin:name}.
     */
    String getName();

    /**
     * Returns the description of the root command node or the command itself.
     */
    DescriptionFacade getDescription();

    /**
     * Returns whether this command is an alias of another command.
     */
    boolean isAlias();

    /**
     * Returns whether this command is visible at all to the given sender.
     *
     * @param sender the sender to check visibility for
     * @return whether this command is visible to the given sender
     */
    boolean isVisible(final CommandSender sender);

    /**
     * Returns the help information for this command.
     * The result is affected by the permissions of the sender.
     *
     * @param sender the sender to get the help for
     * @param query the query to get the help for
     * @return the help information for this command
     */
    CommandHelp getHelp(final CommandSender sender, final String query);

    /**
     * Returns the plugin that owns this command.
     */
    @Nullable MindustryPlugin getPlugin();
}
