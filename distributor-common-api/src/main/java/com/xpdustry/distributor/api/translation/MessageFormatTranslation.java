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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

record MessageFormatTranslation(MessageFormat format) implements Translation {

    private static final int MAX_NAMED_INDEX = 63;

    @Override
    public String formatArray(final List<Object> args) {
        return format.format(args.toArray());
    }

    @Override
    public String formatArray(Object... args) {
        return format.format(args);
    }

    @Override
    public String formatNamed(final Map<String, Object> args) {
        final List<Object> entries = new ArrayList<>();
        for (final var entry : args.entrySet()) {
            final int index;
            try {
                index = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException ignored) {
                continue;
            }
            if (index > MAX_NAMED_INDEX) {
                throw new IllegalArgumentException(
                        "Max argument index exceeded, expected less than " + MAX_NAMED_INDEX + ", got " + index);
            }
            for (int i = entries.size(); i <= index; i++) {
                entries.add(null);
            }
            entries.set(index, entry.getValue());
        }
        return format.format(entries.toArray());
    }

    @Override
    public String formatEmpty() {
        return format.toPattern();
    }
}
