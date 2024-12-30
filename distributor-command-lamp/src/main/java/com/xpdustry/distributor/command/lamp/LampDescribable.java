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

import com.xpdustry.distributor.api.command.DescriptionFacade;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.node.CommandNode;
import revxrsal.commands.node.ParameterNode;

@DistributorDataClass
@Value.Immutable
public interface LampDescribable<A extends MindustryCommandActor> {

    static <A extends MindustryCommandActor> DescriptionMapper<LampDescribable<A>> defaultDescriptionMapper() {
        return describable -> {
            if (describable.getNode() instanceof ParameterNode<?, ?> parameter) {
                return DescriptionFacade.text(Objects.requireNonNullElse(parameter.description(), ""));
            } else if (describable.getNode() == null) {
                return DescriptionFacade.text(
                        Objects.requireNonNullElse(describable.getCommand().description(), ""));
            } else {
                return DescriptionFacade.EMPTY;
            }
        };
    }

    static <A extends MindustryCommandActor> LampDescribable<A> of(final ExecutableCommand<A> command) {
        return LampDescribableImpl.of(command, null);
    }

    static <A extends MindustryCommandActor> LampDescribable<A> of(
            final ExecutableCommand<A> command, final CommandNode<A> node) {
        return LampDescribableImpl.of(command, node);
    }

    ExecutableCommand<A> getCommand();

    @Nullable CommandNode<A> getNode();
}
