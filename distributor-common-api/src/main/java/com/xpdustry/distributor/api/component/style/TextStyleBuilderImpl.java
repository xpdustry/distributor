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

import com.xpdustry.distributor.api.permission.TriState;
import java.util.EnumMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextStyleBuilderImpl implements TextStyle.Builder {

    static final TextStyle EMPTY = TextStyleImpl.of(null, null, Map.of());

    private @Nullable ComponentColor textColor = null;
    private @Nullable ComponentColor backColor = null;
    private final Map<TextDecoration, Boolean> decorations = new EnumMap<>(TextDecoration.class);

    @Override
    public TextStyle.Builder setTextColor(final @Nullable ComponentColor color) {
        this.textColor = color;
        return this;
    }

    @Override
    public TextStyle.Builder setBackColor(final @Nullable ComponentColor backColor) {
        this.backColor = backColor;
        return this;
    }

    @Override
    public TextStyle.Builder setDecorations(final Map<TextDecoration, Boolean> decorations) {
        this.decorations.clear();
        this.decorations.putAll(decorations);
        return this;
    }

    @Override
    public TextStyle.Builder setDecoration(TextDecoration decoration, TriState state) {
        if (state == TriState.UNDEFINED) {
            decorations.remove(decoration);
        } else {
            decorations.put(decoration, state.asBoolean());
        }
        return this;
    }

    @Override
    public TextStyle build() {
        return TextStyleImpl.of(textColor, backColor, new EnumMap<>(decorations));
    }
}
