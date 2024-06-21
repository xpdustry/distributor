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

/**
 * A component is an immutable object that represents something that can be displayed to a receiver.
 * This can be a simple text, locale specific objects like dates or numbers or even translations.
 */
public interface Component {

    /**
     * Returns the text textStyle of this component.
     */
    TextStyle getTextStyle();

    /**
     * Compresses this component. Removing empty subcommands and/or merging subcomponents if necessary.
     *
     * @return the compressed component
     */
    default Component compress() {
        return this;
    }
}
