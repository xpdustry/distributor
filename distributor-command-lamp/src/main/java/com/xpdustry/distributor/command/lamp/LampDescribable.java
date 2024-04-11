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

import com.xpdustry.distributor.internal.DistributorDataClass;
import java.util.Objects;
import org.immutables.value.Value;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.command.ExecutableCommand;

public sealed interface LampDescribable {

    String getName();

    String getDescription();

    @DistributorDataClass
    @Value.Immutable
    sealed interface Command extends LampDescribable permits CommandImpl {

        static Command of(final ExecutableCommand command) {
            return CommandImpl.of(command);
        }

        ExecutableCommand getLampCommand();

        @Override
        default String getName() {
            return getLampCommand().getName();
        }

        @Override
        default String getDescription() {
            return Objects.requireNonNullElse(getLampCommand().getDescription(), "");
        }
    }

    @DistributorDataClass
    @Value.Immutable
    sealed interface Parameter extends LampDescribable permits ParameterImpl {

        static Parameter of(final ExecutableCommand command, final CommandParameter parameter) {
            return ParameterImpl.of(Command.of(command), parameter);
        }

        LampDescribable.Command getCommand();

        CommandParameter getLampParameter();

        @Override
        default String getName() {
            return getLampParameter().getName();
        }

        @Override
        default String getDescription() {
            return Objects.requireNonNullElse(getLampParameter().getDescription(), "");
        }
    }
}
