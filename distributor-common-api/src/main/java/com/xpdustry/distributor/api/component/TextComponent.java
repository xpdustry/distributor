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
import com.xpdustry.distributor.api.component.style.ComponentStyle;

public interface TextComponent
        extends BuildableComponent<TextComponent, TextComponent.Builder>, ValueComponent<String> {

    static TextComponent.Builder text() {
        return new TextComponentImpl.Builder();
    }

    static TextComponent text(final String content) {
        return new TextComponentImpl(ComponentStyle.empty(), content);
    }

    static TextComponent text(final String content, final ComponentColor textColor) {
        return new TextComponentImpl(ComponentStyle.style(textColor), content);
    }

    static TextComponent text(final String content, final ComponentStyle style) {
        return new TextComponentImpl(style, content);
    }

    static TextComponent space() {
        return TextComponentImpl.SPACE;
    }

    static TextComponent empty() {
        return TextComponentImpl.EMPTY;
    }

    static TextComponent newline() {
        return TextComponentImpl.NEWLINE;
    }

    String getContent();

    @Override
    default String getValue() {
        return getContent();
    }

    @Override
    default Builder toBuilder() {
        return new TextComponentImpl.Builder(this);
    }

    interface Builder extends BuildableComponent.Builder<TextComponent, TextComponent.Builder> {

        Builder setContent(final String content);
    }
}
