/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.util;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A ternary boolean type, used by the permission system.
 */
public enum Tristate {
    FALSE(false),
    TRUE(true),
    UNDEFINED(false);

    private final boolean value;

    Tristate(final boolean value) {
        this.value = value;
    }

    public static Tristate of(final @Nullable Boolean state) {
        return state == null ? UNDEFINED : state ? TRUE : FALSE;
    }

    public boolean asBoolean() {
        return this.value;
    }
}
