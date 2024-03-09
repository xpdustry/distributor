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

import com.xpdustry.distributor.common.internal.GeneratedDataClass;
import java.util.Collection;
import org.immutables.value.Value;

public sealed interface CommandElement {

    String getName();

    CommandDescription getDescription();

    Collection<String> getAliases();

    @SuppressWarnings("immutables:subtype")
    @GeneratedDataClass
    @Value.Immutable
    sealed interface Argument extends CommandElement permits ImmutableArgument {

        static Argument of(
                final String name,
                final CommandDescription description,
                final Collection<String> aliases,
                final Kind kind) {
            return ImmutableArgument.of(name, description, aliases, kind);
        }

        Kind getKind();

        enum Kind {
            LITERAL,
            REQUIRED,
            OPTIONAL,
        }
    }

    @SuppressWarnings("immutables:subtype")
    @GeneratedDataClass
    @Value.Immutable
    sealed interface Flag extends CommandElement permits ImmutableFlag {

        static Flag of(
                final String name,
                final CommandDescription description,
                final Collection<String> aliases,
                final boolean repeatable) {
            return ImmutableFlag.of(name, description, aliases, repeatable);
        }

        boolean isRepeatable();
    }
}
