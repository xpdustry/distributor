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
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MindustryCommandFacade implements CommandFacade {

    private final CommandHandler.Command command;
    private final DescriptionFacade description;

    MindustryCommandFacade(final CommandHandler.Command command) {
        this.command = command;
        this.description = DescriptionFacade.text(command.description);
    }

    @Override
    public String getRealName() {
        return command.text;
    }

    @Override
    public String getName() {
        return command.text;
    }

    @Override
    public DescriptionFacade getDescription() {
        return this.description;
    }

    @Override
    public boolean isAlias() {
        return false;
    }

    @Override
    public boolean isPrefixed() {
        return false;
    }

    @Override
    public boolean isVisible(final CommandSender sender) {
        return true;
    }

    @Override
    public CommandHelp getHelp(final CommandSender sender, final String query) {
        return CommandHelp.Entry.of(
                command.params.length == 0 ? command.text : command.text + " " + command.paramText,
                getDescription(),
                DescriptionFacade.EMPTY,
                Arrays.stream(command.params)
                        .map(p -> CommandElement.Argument.of(
                                p.name,
                                DescriptionFacade.EMPTY,
                                List.of(),
                                p.optional
                                        ? CommandElement.Argument.Kind.OPTIONAL
                                        : CommandElement.Argument.Kind.REQUIRED))
                        .toList(),
                List.of());
    }

    @Override
    public @Nullable MindustryPlugin getPlugin() {
        return null;
    }
}
