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
import com.xpdustry.distributor.command.cloud.ArcCommandContextKeys;
import com.xpdustry.distributor.common.collection.ArcCollections;
import java.util.concurrent.CompletableFuture;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

public final class PlayerParser<C> implements ArgumentParser<C, Player> {

    static SuggestionProvider<?> SUGGESTION_PROVIDER = (ctx, input) -> CompletableFuture.supplyAsync(
            () -> ArcCollections.immutableList(Groups.player).stream()
                    .map(Player::plainName)
                    .map(Suggestion::simple)
                    .toList(),
            Core.app::post);

    public static <C> ParserDescriptor<C, Player> playerParser() {
        return ParserDescriptor.of(new PlayerParser<>(), Player.class);
    }

    public static <C> CommandComponent.Builder<C, Player> playerComponent() {
        return CommandComponent.<C, Player>builder().parser(playerParser());
    }

    @Override
    public ArgumentParseResult<Player> parse(final CommandContext<C> ctx, final CommandInput input) {
        final var query = input.readString();
        final var players =
                PlayerLookup.findOnlinePlayers(query, ctx.getOrDefault(ArcCommandContextKeys.MINDUSTRY_ADMIN, false));
        if (players.isEmpty()) {
            return ArgumentParseResult.failure(new PlayerParseException.PlayerNotFound(PlayerParser.class, query, ctx));
        } else if (players.size() > 1) {
            return ArgumentParseResult.failure(new PlayerParseException.TooManyPlayers(PlayerParser.class, query, ctx));
        } else {
            return ArgumentParseResult.success(players.iterator().next());
        }
    }

    @Override
    public @NonNull CompletableFuture<@NonNull ArgumentParseResult<Player>> parseFuture(
            final CommandContext<C> ctx, final CommandInput input) {
        return CompletableFuture.supplyAsync(() -> this.parse(ctx, input), Core.app::post);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (SuggestionProvider<C>) SUGGESTION_PROVIDER;
    }
}
