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

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassSingleton;
import java.util.List;
import org.immutables.value.Value;

/**
 * Helper class for creating help commands that can work with any command system.
 */
public sealed interface CommandHelp {

    /**
     * Represents a specific command entry.
     */
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Entry extends CommandHelp {

        /**
         * Creates a new command entry.
         *
         * @param syntax the syntax of the command
         * @param description the description of the command
         * @param verboseDescription the verbose description of the command
         * @param arguments the arguments of the command
         * @param flags the flags of the command
         */
        static Entry of(
                final String syntax,
                final DescriptionFacade description,
                final DescriptionFacade verboseDescription,
                final List<CommandElement.Argument> arguments,
                final List<CommandElement.Flag> flags) {
            return EntryImpl.of(syntax, description, verboseDescription, arguments, flags);
        }

        /**
         * Returns the syntax of the command.
         */
        String getSyntax();

        /**
         * Returns the description of the command.
         */
        DescriptionFacade getDescription();

        /**
         * Returns the verbose description of the command.
         */
        DescriptionFacade getVerboseDescription();

        /**
         * Returns the arguments of the command.
         */
        List<CommandElement.Argument> getArguments();

        /**
         * Returns the flags of the command.
         */
        List<CommandElement.Flag> getFlags();
    }

    /**
     * Represents a list of available commands, as suggestions.
     */
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Suggestion extends CommandHelp {

        /**
         * Creates a new command suggestion.
         *
         * @param longestSharedPath the longest shared path of the suggestions
         * @param childSuggestions the child suggestions
         */
        static Suggestion of(final String longestSharedPath, final List<String> childSuggestions) {
            return SuggestionImpl.of(longestSharedPath, childSuggestions);
        }

        /**
         * Returns the longest shared path of the suggestions. This means for the commands {@code a b c} and {@code a b d},
         * the longest shared path would be {@code a b}.
         */
        String getLongestSharedPath();

        /**
         * Returns the child suggestions.
         */
        List<String> getChildSuggestions();
    }

    @DistributorDataClassSingleton
    @Value.Immutable
    non-sealed interface Empty extends CommandHelp {

        static Empty of() {
            return EmptyImpl.of();
        }
    }
}
