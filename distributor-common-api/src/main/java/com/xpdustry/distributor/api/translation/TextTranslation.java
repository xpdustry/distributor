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

import java.util.List;
import java.util.Map;

record TextTranslation(String text) implements Translation {

    @Override
    public String formatArray(final List<Object> args) {
        return text;
    }

    @Override
    public String formatArray(final Object... args) {
        return text;
    }

    @Override
    public String formatNamed(final Map<String, Object> args) {
        return text;
    }

    @Override
    public String formatEmpty() {
        return text;
    }

    @Override
    public String format(final TranslationParameters parameters) {
        return text;
    }
}
