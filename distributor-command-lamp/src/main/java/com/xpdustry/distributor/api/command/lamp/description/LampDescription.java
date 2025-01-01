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
package com.xpdustry.distributor.api.command.lamp.description;

import com.xpdustry.distributor.api.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Objects;
import org.immutables.value.Value;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.node.CommandNode;
import revxrsal.commands.node.HasDescription;

public sealed interface LampDescription<A extends MindustryCommandActor> {

    String getDescription();

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Command<A extends MindustryCommandActor> extends LampDescription<A> {

        static <A extends MindustryCommandActor> Command<A> of(final ExecutableCommand<A> command) {
            return CommandImpl.of(command);
        }

        ExecutableCommand<A> getCommand();

        @Override
        default String getDescription() {
            return Objects.requireNonNullElse(getCommand().description(), "");
        }

        @DistributorDataClass
        @Value.Immutable
        non-sealed interface Node<A extends MindustryCommandActor> extends LampDescription<A> {

            static <A extends MindustryCommandActor> Node<A> of(final CommandNode<A> node) {
                return NodeImpl.of(node);
            }

            CommandNode<A> getNode();

            @Override
            default String getDescription() {
                return getNode() instanceof HasDescription desc
                        ? Objects.requireNonNullElse(desc.description(), "")
                        : "";
            }
        }
    }
}
