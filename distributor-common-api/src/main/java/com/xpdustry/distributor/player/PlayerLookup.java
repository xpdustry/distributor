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
package com.xpdustry.distributor.player;

import arc.util.Strings;
import com.xpdustry.distributor.internal.DistributorDataClass;
import java.text.Normalizer;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import mindustry.gen.Player;
import org.immutables.value.Value;

public interface PlayerLookup {

    // https://stackoverflow.com/a/4122200
    Function<String, String> DEFAULT_NORMALIZER =
            string -> Normalizer.normalize(Strings.stripColors(string), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase(Locale.ROOT);

    static PlayerLookup create() {
        return new PlayerLookupImpl(DEFAULT_NORMALIZER);
    }

    static PlayerLookup create(final Function<String, String> normalizer) {
        return new PlayerLookupImpl(normalizer);
    }

    Collection<Player> findOnlinePlayers(final Query query);

    @DistributorDataClass
    @Value.Immutable(builder = true, copy = false)
    sealed interface Query permits QueryImpl {

        static Query of(final String input) {
            return builder().setInput(input).build();
        }

        static Query.Builder builder() {
            return QueryImpl.builder()
                    .setFields(EnumSet.of(Field.NAME, Field.ENTITY_ID))
                    .setMatchExact(true);
        }

        String getInput();

        Set<Field> getFields();

        boolean isMatchExact();

        sealed interface Builder permits QueryImpl.Builder {

            Builder setInput(final String queryInput);

            Builder setFields(final Iterable<Field> fields);

            Builder addField(final Field field);

            Builder addAllFields(final Iterable<Field> fields);

            Builder setMatchExact(final boolean matchExact);

            Query build();
        }
    }

    enum Field {
        NAME,
        UUID,
        ENTITY_ID
    }
}
