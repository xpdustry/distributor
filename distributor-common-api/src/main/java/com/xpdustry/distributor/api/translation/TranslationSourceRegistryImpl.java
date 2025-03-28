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
package com.xpdustry.distributor.api.translation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

final class TranslationSourceRegistryImpl implements TranslationSourceRegistry {

    private final Deque<TranslationSource> sources = new ArrayDeque<>();

    @Override
    public void register(final TranslationSource source) {
        this.sources.add(source);
    }

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        final var iterator = this.sources.descendingIterator();

        while (iterator.hasNext()) {
            final var translation = iterator.next().getTranslation(key, locale);
            if (translation != null) {
                return translation;
            }
        }

        return null;
    }
}
