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
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.translation.Translation;
import com.xpdustry.distributor.api.translation.TranslationArguments;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;

record TranslatedDescriptionFacade(String key, Locale defaultLocale) implements DescriptionFacade {

    @Override
    public String getText() {
        return this.getTranslation(null).format(TranslationArguments.empty());
    }

    @Override
    public String getText(final CommandSender sender) {
        return this.getTranslation(sender).format(TranslationArguments.empty());
    }

    @Override
    public Component getComponent() {
        return this.getTranslation(null).formatAsComponent(TranslationArguments.empty());
    }

    @Override
    public Component getComponent(final CommandSender sender) {
        return this.getTranslation(sender).formatAsComponent(TranslationArguments.empty());
    }

    @Override
    public boolean isEmpty() {
        return Distributor.get().getGlobalTranslationSource().getTranslation(this.key, this.defaultLocale) == null;
    }

    @Override
    public boolean isEmpty(final CommandSender sender) {
        return Distributor.get().getGlobalTranslationSource().getTranslation(this.key, sender.getLocale()) == null;
    }

    private Translation getTranslation(final @Nullable CommandSender sender) {
        return Distributor.get()
                .getGlobalTranslationSource()
                .getTranslationOrMissing(this.key, sender != null ? sender.getLocale() : this.defaultLocale);
    }
}
