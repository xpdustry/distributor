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

record TextComponentImpl(TextStyle textStyle, String content) implements TextComponent {

    static final TextComponent EMPTY = new TextComponentImpl(TextStyle.none(), "");
    static final TextComponent SPACE = new TextComponentImpl(TextStyle.none(), " ");
    static final TextComponent NEWLINE = new TextComponentImpl(TextStyle.none(), "\n");

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public TextStyle getTextStyle() {
        return this.textStyle;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder extends AbstractComponentBuilder<TextComponent, TextComponent.Builder>
            implements TextComponent.Builder {

        private String content = "";

        public Builder() {
            super();
        }

        public Builder(final TextComponent component) {
            super(component);
            this.content = component.getContent();
        }

        @Override
        public TextComponent.Builder setContent(final String content) {
            this.content = content;
            return this;
        }

        @Override
        public TextComponent build() {
            return new TextComponentImpl(textStyle.build(), content);
        }
    }
}
