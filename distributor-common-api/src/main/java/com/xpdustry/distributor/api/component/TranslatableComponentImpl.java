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

import com.xpdustry.distributor.api.component.style.TextStyle;
import com.xpdustry.distributor.api.translation.TranslationArguments;
import java.util.Objects;

record TranslatableComponentImpl(TextStyle textStyle, String key, TranslationArguments parameters)
        implements BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, TranslatableComponent {

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public TranslationArguments getParameters() {
        return this.parameters;
    }

    @Override
    public TextStyle getTextStyle() {
        return this.textStyle;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder implements TranslatableComponent.Builder {

        private TextStyle textStyle = TextStyle.of();
        private String key;
        private TranslationArguments parameters;

        public Builder() {
            this.key = "";
            this.parameters = TranslationArguments.empty();
        }

        public Builder(final TranslatableComponent component) {
            this.textStyle = component.getTextStyle();
            this.key = component.getKey();
            this.parameters = component.getParameters();
        }

        @Override
        public Builder setTextStyle(final TextStyle textStyle) {
            this.textStyle = Objects.requireNonNull(textStyle);
            return this;
        }

        @Override
        public Builder setKey(final String key) {
            this.key = key;
            return this;
        }

        @Override
        public Builder setParameters(final TranslationArguments parameters) {
            this.parameters = parameters;
            return this;
        }

        @Override
        public TranslatableComponent build() {
            return new TranslatableComponentImpl(this.textStyle, this.key, this.parameters);
        }
    }
}
