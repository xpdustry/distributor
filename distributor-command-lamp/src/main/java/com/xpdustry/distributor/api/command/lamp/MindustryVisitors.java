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
package com.xpdustry.distributor.api.command.lamp;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.api.command.lamp.annotation.Permission;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.key.CTypeKey;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.command.lamp.actor.ActorMapper;
import com.xpdustry.distributor.api.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.api.command.lamp.actor.MindustryPermissionFactory;
import com.xpdustry.distributor.api.command.lamp.actor.MindustrySenderResolver;
import com.xpdustry.distributor.api.command.lamp.exception.MindustryExceptionHandler;
import com.xpdustry.distributor.api.command.lamp.hooks.MindustryCommandHooks;
import com.xpdustry.distributor.api.command.lamp.parameters.ContentParameterType;
import com.xpdustry.distributor.api.command.lamp.parameters.PlayerParameterType;
import com.xpdustry.distributor.api.command.lamp.parameters.TeamParameterType;
import com.xpdustry.distributor.api.command.lamp.validator.AllTeamValidator;
import mindustry.ctype.MappableContent;
import mindustry.game.Team;
import mindustry.gen.Player;
import revxrsal.commands.Lamp;
import revxrsal.commands.LampBuilderVisitor;
import revxrsal.commands.response.ResponseHandler;

import static revxrsal.commands.response.ResponseHandler.Factory.forTypeAndSubclasses;

public final class MindustryVisitors {

    /**
     * Handles the default Mindustry exceptions
     *
     * @param <A> The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> mindustryExceptionHandler() {
        return builder -> builder.exceptionHandler(new MindustryExceptionHandler<>());
    }

    /**
     * Resolves the sender type {@link com.xpdustry.distributor.api.command.CommandSender} and {@link Player}
     * for parameters that come first in the command.
     *
     * @param <A> The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> mindustrySenderResolver() {
        return builder -> builder.senderResolver(MindustrySenderResolver.INSTANCE);
    }

    /**
     * Registers the following Mindustry parameter types:
     * <ul>
     *     <li>{@link Player}</li>
     *     <li>{@link Team}</li>
     * </ul>
     *
     * @param <A>    The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> mindustryParameterTypes() {
        return builder -> {
            builder.parameterTypes()
                    .addParameterTypeLast(Player.class, new PlayerParameterType())
                    .addParameterTypeLast(Team.class, new TeamParameterType());
            CTypeKey.ALL.forEach(contentType -> registerContentParameterType(builder, contentType));
        };
    }

    /**
     * Registers the following Mindustry parameter validators:
     * <ul>
     *     <li>{@link AllTeamValidator} for {@link Team}</li>
     * </ul>
     *
     * @param <A>    The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> mindustryParameterValidators() {
        return builder -> builder.parameterValidator(Team.class, AllTeamValidator.INSTANCE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends MappableContent> void registerContentParameterType(
            final Lamp.Builder<?> builder, final CTypeKey<T> contentType) {
        builder.parameterTypes()
                .addParameterTypeLast(
                        contentType.getKey().getToken().getRawType(), new ContentParameterType(contentType));
    }

    /**
     * Adds a registration hook that injects Lamp commands into Mindustry.
     *
     * @param <A>    The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> registrationHooks(
            final MindustryPlugin plugin,
            final CommandHandler commandHandler,
            final ActorMapper<A> actorMapper,
            final DescriptionMapper<LampDescribable<A>> descriptionMapper) {
        return builder -> builder.hooks()
                .onCommandRegistered(
                        new MindustryCommandHooks<>(plugin, commandHandler, actorMapper, descriptionMapper));
    }

    /**
     * Adds dependencies and type resolvers for the given plugin object
     *
     * @param plugin Plugin to supply
     * @param <A>    The actor type
     * @return The visitor
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> pluginContextParameters(
            final MindustryPlugin plugin) {
        return builder -> {
            builder.parameterTypes().addContextParameterLast(MindustryPlugin.class, (parameter, context) -> plugin);
            builder.parameterTypes().addContextParameterLast((Class) plugin.getClass(), (parameter, context) -> plugin);
            builder.dependency(MindustryPlugin.class, plugin);
            builder.dependency((Class) plugin.getClass(), plugin);
        };
    }

    /**
     * Adds support for the {@link Permission} annotation
     *
     * @param <A> The actor type
     * @return This visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> mindustryPermissions() {
        return builder -> builder.permissionFactory(MindustryPermissionFactory.INSTANCE);
    }

    /**
     * Register a {@link ResponseHandler} for {@link com.xpdustry.distributor.api.component.Component components}.
     *
     * @param <A> The actor type
     * @return The visitor
     */
    public static <A extends MindustryCommandActor> LampBuilderVisitor<A> componentResponseHandler() {
        return builder -> builder.responseHandler(forTypeAndSubclasses(
                Component.class,
                (response, context) -> context.actor().getCommandSender().reply(response)));
    }
}
