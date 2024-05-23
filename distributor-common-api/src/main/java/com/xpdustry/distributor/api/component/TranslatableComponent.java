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
import com.xpdustry.distributor.api.translation.TranslationArguments;

public interface TranslatableComponent
        extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder> {

    static TranslatableComponent.Builder translatable() {
        return new TranslatableComponentImpl.Builder();
    }

    static TranslatableComponent translatable(final String key, final TranslationArguments parameters) {
        return new TranslatableComponentImpl(ComponentStyle.empty(), key, parameters);
    }

    static TranslatableComponent translatable(
            final String key, final TranslationArguments parameters, final ComponentColor color) {
        return new TranslatableComponentImpl(ComponentStyle.style(color), key, parameters);
    }

    static TranslatableComponent translatable(final String key) {
        return new TranslatableComponentImpl(ComponentStyle.empty(), key, TranslationArguments.empty());
    }

    static TranslatableComponent translatable(final String key, final ComponentColor textColor) {
        return new TranslatableComponentImpl(ComponentStyle.style(textColor), key, TranslationArguments.empty());
    }

    String getKey();

    TranslationArguments getParameters();

    interface Builder extends BuildableComponent.Builder<TranslatableComponent, TranslatableComponent.Builder> {

        Builder setKey(final String key);

        Builder setParameters(final TranslationArguments parameters);
    }
}
