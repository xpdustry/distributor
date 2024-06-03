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
import com.xpdustry.distributor.api.translation.TranslationArguments;

/**
 * A component that displays a translatable text.
 */
public interface TranslatableComponent
        extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder> {

    /**
     * Creates a new translatable component builder.
     */
    static TranslatableComponent.Builder translatable() {
        return new TranslatableComponentImpl.Builder();
    }

    /**
     * Creates a new translatable component with the specified key and parameters.
     *
     * @param key the key
     * @param parameters the parameters
     * @return the translatable component
     */
    static TranslatableComponent translatable(final String key, final TranslationArguments parameters) {
        return new TranslatableComponentImpl(TextStyle.none(), key, parameters);
    }

    /**
     * Creates a new translatable component with the specified key, parameters, and text color.
     *
     * @param key the key
     * @param parameters the parameters
     * @param textColor the text color
     * @return the translatable component
     */
    static TranslatableComponent translatable(
            final String key, final TranslationArguments parameters, final ComponentColor textColor) {
        return new TranslatableComponentImpl(TextStyle.of(textColor), key, parameters);
    }

    /**
     * Creates a new translatable component with the specified key, parameters, and text textStyle.
     *
     * @param key the key
     * @param parameters the parameters
     * @param textStyle the text textStyle
     * @return the translatable component
     */
    static TranslatableComponent translatable(
            final String key, final TranslationArguments parameters, final TextStyle textStyle) {
        return new TranslatableComponentImpl(textStyle, key, parameters);
    }

    /**
     * Creates a new translatable component with the specified key.
     *
     * @param key the key
     * @return the translatable component
     */
    static TranslatableComponent translatable(final String key) {
        return new TranslatableComponentImpl(TextStyle.none(), key, TranslationArguments.empty());
    }

    /**
     * Creates a new translatable component with the specified key and text color.
     *
     * @param key the key
     * @param textColor the text color
     * @return the translatable component
     */
    static TranslatableComponent translatable(final String key, final ComponentColor textColor) {
        return new TranslatableComponentImpl(TextStyle.of(textColor), key, TranslationArguments.empty());
    }

    /**
     * Creates a new translatable component with the specified key and text textStyle.
     *
     * @param key the key
     * @param textStyle the text textStyle
     * @return the translatable component
     */
    static TranslatableComponent translatable(final String key, final TextStyle textStyle) {
        return new TranslatableComponentImpl(textStyle, key, TranslationArguments.empty());
    }

    /**
     * Returns the key of the translation.
     */
    String getKey();

    /**
     * Returns the parameters of the translation.
     */
    TranslationArguments getParameters();

    /**
     * A builder for translatable components.
     */
    interface Builder extends BuildableComponent.Builder<TranslatableComponent, TranslatableComponent.Builder> {

        /**
         * Sets the key of the translation.
         *
         * @param key the key
         * @return this builder
         */
        Builder setKey(final String key);

        /**
         * Sets the parameters of the translation.
         *
         * @param parameters the parameters
         * @return this builder
         */
        Builder setParameters(final TranslationArguments parameters);
    }
}
