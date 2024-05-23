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

import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class TranslationSourceRegistryImplTest {

    @Test
    void test_lookup_order() {
        final var registry = createSource();
        final var source1 = new BundleTranslationSourceImpl(Locale.FRENCH);
        source1.register("greeting", Locale.FRENCH, Translation.text("Bonjour!"));
        final var source2 = RouterTranslationSource.INSTANCE;

        assertThat(registry.getTranslation("greeting", RouterTranslationSource.ROUTER_LOCALE))
                .isNull();

        registry.register(source1);
        assertThat(registry.getTranslation("greeting", RouterTranslationSource.ROUTER_LOCALE))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("Bonjour!");

        registry.register(source2);
        assertThat(registry.getTranslation("greeting", RouterTranslationSource.ROUTER_LOCALE))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("router");
    }

    private TranslationSourceRegistryImpl createSource() {
        return new TranslationSourceRegistryImpl();
    }
}
