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
import java.util.Set;
import org.immutables.value.Value;

/**
 * A {@code CommandElement} represents a part of a given command chain.
 * Allowing the user to read basic information about the internals of a command without having to interact with the underlying command system.
 */
public sealed interface CommandElement {

    /**
     * Returns the name of this command element.
     */
    String getName();

    /**
     * Returns the description of this command element.
     */
    DescriptionFacade getDescription();

    /**
     * Returns the aliases of this command element.
     */
    Set<String> getAliases();

    /**
     * Represents a command argument.
     */
    @SuppressWarnings("immutables:subtype")
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Argument extends CommandElement {

        /**
         * Creates a new command argument.
         *
         * @param name        the name of the argument
         * @param description the description of the argument
         * @param aliases     the aliases of the argument
         * @param kind        the kind of the argument
         * @return the created argument
         */
        static Argument of(
                final String name,
                final DescriptionFacade description,
                final Set<String> aliases,
                final Argument.Kind kind) {
            return ArgumentImpl.of(name, description, aliases, kind);
        }

        /**
         * Returns the kind of this argument.
         */
        Argument.Kind getKind();

        /**
         * Determines how an argument is parsed on a high level.
         */
        enum Kind {
            LITERAL,
            REQUIRED,
            OPTIONAL,
        }
    }

    /**
     * Represents a command flag, usually an optional named argument.
     */
    @SuppressWarnings("immutables:subtype")
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Flag extends CommandElement {

        /**
         * Creates a new command flag.
         *
         * @param name        the name of the flag
         * @param description the description of the flag
         * @param aliases     the aliases of the flag
         * @param kind        the kind of the flag
         * @param mode        the mode of the flag
         * @return the created flag
         */
        static Flag of(
                final String name,
                final DescriptionFacade description,
                final Set<String> aliases,
                final Flag.Kind kind,
                final Flag.Mode mode) {
            return FlagImpl.of(name, description, aliases, kind, mode);
        }

        /**
         * Returns the kind of this flag.
         */
        Flag.Kind getKind();

        /**
         * Returns the mode of this flag.
         */
        Flag.Mode getMode();

        /**
         * Determines how a flag is parsed on a high level.
         * <p>
         * Note that most command systems treat flags as optional arguments.
         */
        enum Kind {
            OPTIONAL,
            REQUIRED
        }

        /**
         * Determines how many times a flag can be used.
         */
        enum Mode {
            SINGLE,
            REPEATABLE
        }
    }
}
