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
package com.xpdustry.distributor.api.util;

import org.jspecify.annotations.Nullable;

/**
 * A ternary boolean type. Equivalent to {@code @Nullable Boolean}, but safer.
 */
public enum TriState {
    FALSE(false),
    TRUE(true),
    UNDEFINED(false);

    public static TriState of(final boolean state) {
        return state ? TRUE : FALSE;
    }

    public static TriState of(final @Nullable Boolean state) {
        return state == null ? UNDEFINED : state ? TRUE : FALSE;
    }

    private final boolean value;

    TriState(final boolean value) {
        this.value = value;
    }

    public boolean asBoolean() {
        return this.value;
    }
}
