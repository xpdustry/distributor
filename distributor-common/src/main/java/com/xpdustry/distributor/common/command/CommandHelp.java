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
package com.xpdustry.distributor.common.command;

import com.xpdustry.distributor.common.internal.DistributorDataClass;
import java.util.List;
import org.immutables.value.Value;

public sealed interface CommandHelp {

    @DistributorDataClass
    @Value.Immutable
    sealed interface Entry extends CommandHelp permits ImmutableEntry {

        static Entry of(
                final String syntax,
                final DescriptionFacade description,
                final DescriptionFacade verboseDescription,
                final List<CommandElement.Argument> arguments,
                final List<CommandElement.Flag> flags) {
            return ImmutableEntry.of(syntax, description, verboseDescription, arguments, flags);
        }

        String getSyntax();

        DescriptionFacade getDescription();

        DescriptionFacade getVerboseDescription();

        List<CommandElement.Argument> getArguments();

        List<CommandElement.Flag> getFlags();
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Suggestion extends CommandHelp permits ImmutableSuggestion {

        static Suggestion of(final String longestSharedPath, final List<String> childSuggestions) {
            return ImmutableSuggestion.of(longestSharedPath, childSuggestions);
        }

        String getLongestSharedPath();

        List<String> getChildSuggestions();
    }

    final class Empty implements CommandHelp {

        private static final Empty INSTANCE = new Empty();

        private Empty() {}

        public static Empty getInstance() {
            return INSTANCE;
        }
    }
}
