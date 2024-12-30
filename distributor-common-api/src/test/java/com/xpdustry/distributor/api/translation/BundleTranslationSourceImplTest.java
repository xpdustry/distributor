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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class BundleTranslationSourceImplTest {

    @Test
    void test_get_translation() {
        final var registry = this.createSource(Locale.ENGLISH);
        registry.register("greeting", Locale.FRENCH, TextTranslation.of("Bonjour"));
        registry.register("greeting", Locale.ENGLISH, TextTranslation.of("Hello"));

        assertThat(registry.getTranslation("greeting", Locale.FRENCH))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("Bonjour");

        assertThat(registry.getTranslation("greeting", Locale.ENGLISH))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("Hello");

        assertThat(registry.getTranslation("greeting", Locale.US))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("Hello");

        assertThat(registry.getTranslation("greeting", Locale.CHINESE))
                .isNotNull()
                .extracting(translation -> translation.format(TranslationArguments.empty()))
                .isEqualTo("Hello");
    }

    @Test
    void test_unregister() {
        final var registry = this.createSource(Locale.ENGLISH);

        registry.register("greeting", Locale.FRENCH, TextTranslation.of("Bonjour"));
        registry.register("greeting", Locale.ENGLISH, TextTranslation.of("Hello"));
        registry.register("greeting", Locale.ITALIAN, TextTranslation.of("Ciao"));

        assertThat(registry.registered("greeting")).isTrue();
        assertThat(registry.getTranslation("greeting", Locale.ENGLISH)).isNotNull();
        assertThat(registry.getTranslation("greeting", Locale.FRENCH)).isNotNull();
        assertThat(registry.getTranslation("greeting", Locale.ITALIAN)).isNotNull();
        assertThat(registry.registered("greeting", Locale.ENGLISH)).isTrue();
        assertThat(registry.registered("greeting", Locale.FRENCH)).isTrue();
        assertThat(registry.registered("greeting", Locale.ITALIAN)).isTrue();

        registry.unregister("greeting", Locale.ENGLISH);
        assertThat(registry.getTranslation("greeting", Locale.ENGLISH)).isNull();
        assertThat(registry.getTranslation("greeting", Locale.FRENCH)).isNotNull();
        assertThat(registry.getTranslation("greeting", Locale.ITALIAN)).isNotNull();
        assertThat(registry.registered("greeting")).isTrue();
        assertThat(registry.registered("greeting", Locale.ENGLISH)).isFalse();
        assertThat(registry.registered("greeting", Locale.FRENCH)).isTrue();
        assertThat(registry.registered("greeting", Locale.ITALIAN)).isTrue();

        registry.unregister("greeting");
        assertThat(registry.getTranslation("greeting", Locale.ENGLISH)).isNull();
        assertThat(registry.getTranslation("greeting", Locale.ENGLISH)).isNull();
        assertThat(registry.getTranslation("greeting", Locale.FRENCH)).isNull();
        assertThat(registry.registered("greeting")).isFalse();
        assertThat(registry.registered("greeting", Locale.ENGLISH)).isFalse();
        assertThat(registry.registered("greeting", Locale.FRENCH)).isFalse();
        assertThat(registry.registered("greeting", Locale.ITALIAN)).isFalse();
    }

    @Test
    void test_illegal_register() {
        final var registry = this.createSource(Locale.ENGLISH);
        registry.register("greeting", Locale.FRENCH, TextTranslation.of("Bonjour"));
        assertThatThrownBy(() -> registry.register("greeting", Locale.FRENCH, TextTranslation.of("Bonjour")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private BundleTranslationSourceImpl createSource(final Locale locale) {
        return new BundleTranslationSourceImpl(locale);
    }
}
