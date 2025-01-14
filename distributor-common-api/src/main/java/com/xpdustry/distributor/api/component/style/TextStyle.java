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

import com.xpdustry.distributor.api.util.TriState;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the styling of the text contained in a component.
 */
public interface TextStyle {

    /**
     * Returns a text style with no styling.
     */
    static TextStyle of() {
        return TextStyleImpl.NONE;
    }

    /**
     * Creates a new text style with the given colors and decorations.
     *
     * @param textColor   the text color
     * @param backColor   the background color
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(
            final @Nullable ComponentColor textColor,
            final @Nullable ComponentColor backColor,
            final Map<TextDecoration, Boolean> decorations) {
        return new TextStyleImpl(textColor, backColor, decorations);
    }

    /**
     * Creates a new text style with the given colors.
     *
     * @param textColor the text color
     * @param backColor the background color
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor, final @Nullable ComponentColor backColor) {
        return new TextStyleImpl(textColor, backColor, Map.of());
    }

    /**
     * Creates a new text style with the given text color.
     *
     * @param textColor the text color
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor) {
        return new TextStyleImpl(textColor, null, Map.of());
    }

    /**
     * Creates a new text style with the given text color and decorations.
     *
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor, final TextDecoration... decorations) {
        return new TextStyleImpl(
                textColor,
                null,
                Arrays.stream(decorations).collect(Collectors.toMap(Function.identity(), decoration -> true)));
    }

    /**
     * Creates a new text style with the given decorations.
     *
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(final Map<TextDecoration, Boolean> decorations) {
        return new TextStyleImpl(null, null, decorations);
    }

    /**
     * Creates a new text style with the given decorations.
     *
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(final TextDecoration... decorations) {
        return new TextStyleImpl(
                null,
                null,
                Arrays.stream(decorations).collect(Collectors.toMap(Function.identity(), decoration -> true)));
    }

    /**
     * Returns the text color of this style.
     */
    @Nullable ComponentColor getTextColor();

    /**
     * Sets the text color.
     *
     * @param textColor the text color
     * @return a new text style with the given text color
     */
    TextStyle setTextColor(final @Nullable ComponentColor textColor);

    /**
     * Returns the background color of this style.
     */
    @Nullable ComponentColor getBackColor();

    /**
     * Sets the background color.
     *
     * @param backColor the background color
     * @return a new text style with the given background color
     */
    TextStyle setBackColor(final @Nullable ComponentColor backColor);

    /**
     * Returns the decorations of this style.
     */
    Map<TextDecoration, Boolean> getDecorations();

    /**
     * Sets the decorations.
     *
     * @param decorations the decorations
     * @return a new text style with the given decorations
     */
    TextStyle setDecorations(final Map<TextDecoration, Boolean> decorations);

    /**
     * Adds a decoration.
     *
     * @param decoration the decoration
     * @return this setter or a copy
     */
    default TextStyle addDecoration(final TextDecoration decoration) {
        return this.setDecoration(decoration, TriState.TRUE);
    }

    /**
     * Sets a decoration state. If the state is {@link TriState#UNDEFINED}, the decoration is removed.
     *
     * @param decoration the decoration
     * @param state      the state
     * @return a new text style with the given decoration state
     */
    default TextStyle setDecoration(final TextDecoration decoration, final TriState state) {
        final var copy = new HashMap<>(this.getDecorations());
        if (state == TriState.UNDEFINED) {
            copy.remove(decoration);
        } else {
            copy.put(decoration, state.asBoolean());
        }
        return this.setDecorations(copy);
    }

    /**
     * Merges this text style with another one, overriding existing values.
     *
     * @param other the style to merge
     * @return the merged text style
     */
    TextStyle merge(final TextStyle other);
}
