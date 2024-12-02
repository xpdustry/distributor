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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.translation.TranslationArguments;
import java.util.Locale;

record TranslatedDescriptionFacade(String key, Locale defaultLocale) implements DescriptionFacade {

    @Override
    public String getText() {
        return Distributor.get()
                .getGlobalTranslationSource()
                .getTranslationOrMissing(key, this.defaultLocale)
                .format(TranslationArguments.empty());
    }

    @Override
    public String getText(final CommandSender sender) {
        return Distributor.get()
                .getGlobalTranslationSource()
                .getTranslationOrMissing(key, sender.getLocale())
                .format(TranslationArguments.empty());
    }

    @Override
    public boolean isEmpty() {
        return Distributor.get().getGlobalTranslationSource().getTranslation(key, this.defaultLocale) == null;
    }

    @Override
    public boolean isEmpty(final CommandSender sender) {
        return Distributor.get().getGlobalTranslationSource().getTranslation(key, sender.getLocale()) == null;
    }
}
