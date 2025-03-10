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
package com.xpdustry.distributor.api.component.style;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

record TextStyleImpl(
        @Nullable ComponentColor textColor,
        @Nullable ComponentColor backColor,
        Map<TextDecoration, Boolean> decorations)
        implements TextStyle {
    static final TextStyle NONE = new TextStyleImpl(null, null, Map.of());

    @Override
    public @Nullable ComponentColor getTextColor() {
        return this.textColor;
    }

    @Override
    public TextStyle setTextColor(final @Nullable ComponentColor textColor) {
        return new TextStyleImpl(textColor, this.backColor, this.decorations);
    }

    @Override
    public @Nullable ComponentColor getBackColor() {
        return this.backColor;
    }

    @Override
    public TextStyle setBackColor(final @Nullable ComponentColor backColor) {
        return new TextStyleImpl(this.textColor, backColor, this.decorations);
    }

    @Override
    public Map<TextDecoration, Boolean> getDecorations() {
        return this.decorations;
    }

    @Override
    public TextStyle setDecorations(final Map<TextDecoration, Boolean> decorations) {
        final var copy = new EnumMap<TextDecoration, Boolean>(TextDecoration.class);
        copy.putAll(this.decorations);
        return new TextStyleImpl(this.textColor, this.backColor, Collections.unmodifiableMap(copy));
    }

    @Override
    public TextStyle merge(final TextStyle other) {
        var textColor = this.textColor;
        if (other.getTextColor() != null) {
            textColor = other.getTextColor();
        }
        var backColor = this.backColor;
        if (other.getBackColor() != null) {
            backColor = other.getBackColor();
        }
        final var decorations = new EnumMap<TextDecoration, Boolean>(TextDecoration.class);
        decorations.putAll(this.decorations);
        decorations.putAll(other.getDecorations());
        return new TextStyleImpl(textColor, backColor, Collections.unmodifiableMap(decorations));
    }
}
