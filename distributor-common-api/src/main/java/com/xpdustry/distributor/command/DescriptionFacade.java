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
package com.xpdustry.distributor.command;

import com.xpdustry.distributor.internal.DistributorDataClass;
import com.xpdustry.distributor.translation.LocaleHolder;
import java.util.Locale;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public interface DescriptionFacade {

    DescriptionFacade EMPTY = DescriptionFacade.text("");

    static DescriptionFacade text(final String text) {
        return DescriptionFacadeImpl.of(text);
    }

    static DescriptionFacade translation(final String key, final Locale defaultLocale) {
        return new TranslationDescriptionFacade(key, defaultLocale);
    }

    String getText();

    default String getText(final LocaleHolder holder) {
        return this.getText();
    }

    default boolean isEmpty() {
        return this.getText().isEmpty();
    }

    default boolean isEmpty(final LocaleHolder holder) {
        return this.getText(holder).isEmpty();
    }
}
