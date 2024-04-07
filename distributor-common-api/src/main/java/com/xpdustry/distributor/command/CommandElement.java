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

import com.xpdustry.distributor.internal.DistributorDataClass;
import java.util.Collection;
import org.immutables.value.Value;

public sealed interface CommandElement {

    String getName();

    DescriptionFacade getDescription();

    Collection<String> getAliases();

    @SuppressWarnings("immutables:subtype")
    @DistributorDataClass
    @Value.Immutable
    sealed interface Argument extends CommandElement permits ArgumentImpl {

        static Argument of(
                final String name,
                final DescriptionFacade description,
                final Collection<String> aliases,
                final Kind kind) {
            return ArgumentImpl.of(name, description, aliases, kind);
        }

        Kind getKind();

        enum Kind {
            LITERAL,
            REQUIRED,
            OPTIONAL,
        }
    }

    @SuppressWarnings("immutables:subtype")
    @DistributorDataClass
    @Value.Immutable
    sealed interface Flag extends CommandElement permits FlagImpl {

        static Flag of(
                final String name,
                final DescriptionFacade description,
                final Collection<String> aliases,
                final boolean repeatable) {
            return FlagImpl.of(name, description, aliases, repeatable);
        }

        boolean isRepeatable();
    }
}
