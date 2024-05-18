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

import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.translation.TranslationArguments;

record TranslatableComponentImpl(ComponentStyle style, String key, TranslationArguments parameters)
        implements BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, TranslatableComponent {

    @Override
    public TranslatableComponent.Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public TranslationArguments getParameters() {
        return this.parameters;
    }

    @Override
    public ComponentStyle getStyle() {
        return this.style;
    }

    static final class Builder extends AbstractComponent.Builder<TranslatableComponent, TranslatableComponent.Builder>
            implements TranslatableComponent.Builder {

        private String key;
        private TranslationArguments parameters;

        public Builder() {
            this.key = "";
            this.parameters = TranslationArguments.empty();
        }

        public Builder(final TranslatableComponent component) {
            super(component);
            this.key = component.getKey();
            this.parameters = component.getParameters();
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
            return new TranslatableComponentImpl(style.build(), this.key, this.parameters);
        }
    }
}
