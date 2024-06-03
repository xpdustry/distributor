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
package com.xpdustry.distributor.api.component;

import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.TextDecoration;
import com.xpdustry.distributor.api.component.style.TextStyle;
import com.xpdustry.distributor.api.util.TriState;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends BuildableComponent.Builder<C, B>>
        implements BuildableComponent.Builder<C, B> {

    protected TextStyle.Builder textStyle;

    public AbstractComponentBuilder(final C component) {
        this.textStyle = component.getTextStyle().toBuilder();
    }

    public AbstractComponentBuilder() {
        this.textStyle = TextStyle.builder();
    }

    @SuppressWarnings("unchecked")
    @Override
    public B setTextColor(final @Nullable ComponentColor textColor) {
        textStyle.setTextColor(textColor);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public B setBackColor(final @Nullable ComponentColor backColor) {
        textStyle.setBackColor(backColor);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public B setDecorations(final Map<TextDecoration, Boolean> decorations) {
        textStyle.setDecorations(decorations);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public B setDecoration(final TextDecoration decoration, final TriState state) {
        textStyle.setDecoration(decoration, state);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public B setTextStyle(final TextStyle textStyle) {
        this.textStyle = textStyle.toBuilder();
        return (B) this;
    }
}
