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
package com.xpdustry.distributor.common.translation;

import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.translation.Translation;
import com.xpdustry.distributor.api.translation.TranslationSource;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

public final class ServiceTranslationSource implements TranslationSource {

    private final ServiceManager services;

    public ServiceTranslationSource(final ServiceManager services) {
        this.services = services;
    }

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        for (final var provider : this.services.getProviders(TranslationSource.class)) {
            final var translation = provider.getInstance().getTranslation(key, locale);
            if (translation != null) return translation;
        }
        return null;
    }
}
