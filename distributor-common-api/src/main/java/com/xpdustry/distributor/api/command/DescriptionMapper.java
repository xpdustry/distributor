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
package com.xpdustry.distributor.api.command;

import java.util.function.Function;

/**
 * Some command systems already provide some kind of abstraction for descriptions.
 * So this interface is used to map these descriptions to the common {@link DescriptionFacade}.
 */
@FunctionalInterface
public interface DescriptionMapper<T> {

    /**
     * Maps a describable object to a text description by extracting the text from it.
     *
     * @param extractor the function to extract the text from the describable object
     * @param <T>       the type of the describable object
     * @return the created description mapper
     */
    static <T> DescriptionMapper<T> text(final Function<T, String> extractor) {
        return describable -> DescriptionFacade.text(extractor.apply(describable));
    }

    /**
     * Maps a describable object to a description facade.
     *
     * @param describable the describable object
     * @return the created description facade
     */
    DescriptionFacade map(final T describable);
}
