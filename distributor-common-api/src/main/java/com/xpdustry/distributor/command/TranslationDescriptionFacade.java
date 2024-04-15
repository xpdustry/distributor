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

import com.xpdustry.distributor.DistributorProvider;
import com.xpdustry.distributor.translation.LocaleHolder;
import java.util.Locale;

record TranslationDescriptionFacade(String key, Locale defaultLocale) implements DescriptionFacade {

    @Override
    public String getText() {
        return DistributorProvider.get()
                .getGlobalTranslationSource()
                .getTranslationOrDefault(key, this.defaultLocale)
                .formatEmpty();
    }

    @Override
    public String getText(final LocaleHolder holder) {
        return DistributorProvider.get()
                .getGlobalTranslationSource()
                .getTranslationOrDefault(key, holder.getLocale())
                .formatEmpty();
    }

    @Override
    public boolean isEmpty() {
        return DistributorProvider.get().getGlobalTranslationSource().getTranslation(key, this.defaultLocale) == null;
    }

    @Override
    public boolean isEmpty(final LocaleHolder holder) {
        return DistributorProvider.get().getGlobalTranslationSource().getTranslation(key, holder.getLocale()) == null;
    }
}
