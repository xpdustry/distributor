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
package com.xpdustry.distributor.command.cloud.parser;

import arc.Core;
import com.xpdustry.distributor.core.DistributorProvider;
import com.xpdustry.distributor.core.collection.ArcCollections;
import com.xpdustry.distributor.core.player.PlayerLookup;
import java.util.concurrent.CompletableFuture;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

public final class PlayerInfoParser<C> implements ArgumentParser.FutureArgumentParser<C, Administration.PlayerInfo> {

    public static <C> ParserDescriptor<C, Administration.PlayerInfo> playerInfoParser() {
        return ParserDescriptor.of(new PlayerInfoParser<>(), Administration.PlayerInfo.class);
    }

    public static <C> CommandComponent.Builder<C, Administration.PlayerInfo> playerInfoComponent() {
        return CommandComponent.<C, Administration.PlayerInfo>builder().parser(playerInfoParser());
    }

    @Override
    public CompletableFuture<ArgumentParseResult<Administration.PlayerInfo>> parseFuture(
            final CommandContext<C> ctx, final CommandInput input) {
        final var query = input.readString();
        return DistributorProvider.get()
                .getService(PlayerLookup.class)
                .orElseThrow()
                .findOfflinePlayers(query, true)
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        return ArgumentParseResult.failure(
                                new PlayerParseException.PlayerNotFound(PlayerInfoParser.class, query, ctx));
                    } else if (result.size() > 1) {
                        return ArgumentParseResult.failure(
                                new PlayerParseException.TooManyPlayers(PlayerInfoParser.class, query, ctx));
                    } else {
                        return ArgumentParseResult.success(result.get(0));
                    }
                });
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (ctx, input) -> CompletableFuture.supplyAsync(
                () -> ArcCollections.immutableList(Groups.player).stream()
                        .map(Player::plainName)
                        .map(Suggestion::simple)
                        .toList(),
                Core.app::post);
    }
}
