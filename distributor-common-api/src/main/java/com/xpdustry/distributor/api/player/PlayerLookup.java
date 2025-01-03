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
package com.xpdustry.distributor.api.player;

import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassWithBuilder;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.immutables.value.Value;

/**
 * A simple interface for looking up online players.
 */
public interface PlayerLookup {

    /**
     * Creates a new simple player lookup.
     *
     * @see SimplePlayerLookup
     */
    static PlayerLookup create() {
        return new SimplePlayerLookup();
    }

    /**
     * Finds online players matching the given query.
     * Defaults to searching all players.
     *
     * @param query the query
     * @return the matching players
     */
    default List<Player> findOnlinePlayers(final Query query) {
        return this.findOnlinePlayers(MindustryCollections.immutableList(Groups.player), query);
    }

    /**
     * Finds online players matching the given query.
     *
     * @param players the players to search
     * @param query   the query
     * @return the matching players
     */
    List<Player> findOnlinePlayers(final Collection<Player> players, final Query query);

    /**
     * A query for looking up players.
     */
    @DistributorDataClassWithBuilder
    @Value.Immutable
    interface Query {

        /**
         * Creates a simple query with the given input.
         *
         * @param input the input
         * @return the created query
         */
        static Query of(final String input) {
            return builder().setInput(input).build();
        }

        /**
         * Creates a new query builder.
         */
        static Query.Builder builder() {
            return QueryImpl.builder();
        }

        /**
         * The input string to search for.
         */
        String getInput();

        /**
         * The fields to take into account when searching.
         * Defaults to {@link Field#NAME} and {@link Field#ENTITY_ID}.
         */
        @Value.Default
        default Set<Field> getFields() {
            return EnumSet.of(Field.NAME, Field.ENTITY_ID);
        }

        /**
         * Whether to return a singular result if an exact match is found.
         * Defaults to {@code true}.
         */
        @Value.Default
        default boolean isMatchExact() {
            return true;
        }

        /**
         * A builder for creating queries.
         */
        interface Builder {

            Builder setInput(final String queryInput);

            default Builder setFields(final Field... fields) {
                return this.setFields(Set.of(fields));
            }

            Builder setFields(final Iterable<Field> fields);

            Builder addField(final Field field);

            Builder addAllFields(final Iterable<Field> fields);

            Builder setMatchExact(final boolean matchExact);

            Query build();
        }
    }

    /**
     * The fields to take into account when searching.
     */
    enum Field {
        NAME,
        UUID,
        ENTITY_ID,
        /**
         * This one is for server specific identifiers.
         */
        SERVER_ID
    }
}
