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
package com.xpdustry.distributor.api.translation;

import com.xpdustry.distributor.api.component.render.ComponentAppendable;
import com.xpdustry.distributor.api.metadata.MetadataContainer;

/**
 * A translation that can format to a {@link ComponentAppendable}.
 */
public interface ComponentAwareTranslation extends Translation {

    @Override
    default String format(final TranslationArguments parameters) {
        final var appendable = ComponentAppendable.plain(MetadataContainer.empty());
        formatTo(parameters, appendable);
        return appendable.toString();
    }

    /**
     * Formats the translation to the given {@link ComponentAppendable}.
     *
     * @param parameters the translation parameters
     * @param appendable the appendable
     */
    void formatTo(final TranslationArguments parameters, final ComponentAppendable appendable);
}
