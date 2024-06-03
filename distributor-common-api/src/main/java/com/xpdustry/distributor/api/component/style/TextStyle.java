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

import com.xpdustry.distributor.api.util.Buildable;
import com.xpdustry.distributor.api.util.TriState;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

/**
 * Represents the styling of the text contained in a component.
 */
@DistributorDataClass
@Value.Immutable
public interface TextStyle extends Buildable<TextStyle, TextStyle.Builder> {

    /**
     * Creates a new text style with the given colors and decorations.
     *
     * @param textColor the text color
     * @param backColor the background color
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(
            final @Nullable ComponentColor textColor,
            final @Nullable ComponentColor backColor,
            final Map<TextDecoration, Boolean> decorations) {
        return TextStyleImpl.of(textColor, backColor, decorations);
    }

    /**
     * Creates a new text style with the given colors.
     *
     * @param textColor the text color
     * @param backColor the background color
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor, final @Nullable ComponentColor backColor) {
        return TextStyleImpl.of(textColor, backColor, Map.of());
    }

    /**
     * Creates a new text style with the given text color.
     *
     * @param textColor the text color
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor) {
        return TextStyleImpl.of(textColor, null, Map.of());
    }

    /**
     * Creates a new text style with the given text color and decorations.
     *
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(final @Nullable ComponentColor textColor, final TextDecoration... decorations) {
        return TextStyleImpl.of(
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
        return TextStyleImpl.of(null, null, decorations);
    }

    /**
     * Creates a new text style with the given decorations.
     *
     * @param decorations the decorations
     * @return the text style
     */
    static TextStyle of(final TextDecoration... decorations) {
        return TextStyleImpl.of(
                null,
                null,
                Arrays.stream(decorations).collect(Collectors.toMap(Function.identity(), decoration -> true)));
    }

    /**
     * Returns a text style with no styling.
     */
    static TextStyle none() {
        return TextStyleBuilderImpl.EMPTY;
    }

    /**
     * Creates a new text style builder.
     */
    static TextStyle.Builder builder() {
        return new TextStyleBuilderImpl();
    }

    /**
     * Returns the text color of this style.
     */
    @Nullable ComponentColor getTextColor();

    /**
     * Returns the background color of this style.
     */
    @Nullable ComponentColor getBackColor();

    /**
     * Returns the decorations of this style.
     */
    Map<TextDecoration, Boolean> getDecorations();

    /**
     * Merges this text style with another one, overriding existing values.
     *
     * @param other the style to merge
     * @return the merged text style
     */
    default TextStyle merge(final TextStyle other) {
        final var builder = toBuilder();
        if (other.getTextColor() != null) {
            builder.setTextColor(other.getTextColor());
        }
        if (other.getBackColor() != null) {
            builder.setBackColor(other.getBackColor());
        }
        final var decorations = new HashMap<>(getDecorations());
        decorations.putAll(other.getDecorations());
        builder.setDecorations(decorations);
        return builder.build();
    }

    @Override
    default Builder toBuilder() {
        return new TextStyleBuilderImpl()
                .setTextColor(getTextColor())
                .setBackColor(getBackColor())
                .setDecorations(getDecorations());
    }

    /**
     * A text style builder.
     */
    interface Builder extends Setter<Builder>, Buildable.Builder<TextStyle, Builder> {}

    /**
     * Represents an object that can set text style properties.
     *
     * @param <T> the type of the setter
     */
    interface Setter<T extends Setter<T>> {

        /**
         * Sets the text color.
         *
         * @param textColor the text color
         * @return this setter
         */
        T setTextColor(final @Nullable ComponentColor textColor);

        /**
         * Sets the background color.
         *
         * @param backColor the background color
         * @return this setter
         */
        T setBackColor(final @Nullable ComponentColor backColor);

        /**
         * Adds a decoration.
         *
         * @param decoration the decoration
         * @return this setter
         */
        default T addDecoration(final TextDecoration decoration) {
            return setDecoration(decoration, TriState.TRUE);
        }

        /**
         * Sets the decorations.
         *
         * @param decorations the decorations
         * @return this setter
         */
        T setDecorations(final Map<TextDecoration, Boolean> decorations);

        /**
         * Sets a decoration state. If the state is {@link TriState#UNDEFINED}, the decoration is removed.
         *
         * @param decoration the decoration
         * @param state the state
         * @return this setter
         */
        T setDecoration(final TextDecoration decoration, final TriState state);
    }
}
