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
import com.xpdustry.distributor.api.component.style.TextStyle;

/**
 * A component that displays text.
 */
public interface TextComponent extends BuildableComponent<TextComponent, TextComponent.Builder> {

    /**
     * Creates a new text component builder.
     */
    static TextComponent.Builder text() {
        return new TextComponentImpl.Builder();
    }

    /**
     * Creates a new text component with the specified content.
     *
     * @param content the content
     * @return the text component
     */
    static TextComponent text(final String content) {
        return new TextComponentImpl(TextStyle.of(), content);
    }

    /**
     * Creates a new text component with the specified content and text color.
     *
     * @param content   the content
     * @param textColor the text color
     * @return the text component
     */
    static TextComponent text(final String content, final ComponentColor textColor) {
        return new TextComponentImpl(TextStyle.of(textColor), content);
    }

    /**
     * Creates a new text component with the specified content and text textStyle.
     *
     * @param content   the content
     * @param textStyle the text textStyle
     * @return the text component
     */
    static TextComponent text(final String content, final TextStyle textStyle) {
        return new TextComponentImpl(textStyle, content);
    }

    /**
     * Creates a new text component with the specified character.
     *
     * @param ch the character
     * @return the text component
     */
    static TextComponent text(final char ch) {
        return new TextComponentImpl(TextStyle.of(), String.valueOf(ch));
    }

    /**
     * Creates a new text component with the specified character and text color.
     *
     * @param ch        the character
     * @param textColor the text color
     * @return the text component
     */
    static TextComponent text(final char ch, final ComponentColor textColor) {
        return new TextComponentImpl(TextStyle.of(textColor), String.valueOf(ch));
    }

    /**
     * Creates a new text component with the specified character and text textStyle.
     *
     * @param ch        the character
     * @param textStyle the text textStyle
     * @return the text component
     */
    static TextComponent text(final char ch, final TextStyle textStyle) {
        return new TextComponentImpl(textStyle, String.valueOf(ch));
    }

    /**
     * Returns a text component with only a space character.
     */
    static TextComponent space() {
        return TextComponentImpl.SPACE;
    }

    /**
     * Returns a text component with a newline character.
     */
    static TextComponent newline() {
        return TextComponentImpl.NEWLINE;
    }

    /**
     * Returns a text component with no content.
     */
    static TextComponent empty() {
        return TextComponentImpl.EMPTY;
    }

    /**
     * Returns the content of this component.
     */
    String getContent();

    /**
     * A builder for text components.
     */
    interface Builder extends BuildableComponent.Builder<TextComponent, TextComponent.Builder> {

        /**
         * Sets the content of the component.
         *
         * @param content the content
         * @return this builder
         */
        Builder setContent(final String content);
    }
}
