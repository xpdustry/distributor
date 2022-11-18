/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.MessageFormat;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class LocalizationSourceRegistryImplTest {

    private LocalizationSourceRegistryImpl registry;

    @BeforeEach
    void setup() {
        this.registry = new LocalizationSourceRegistryImpl(Locale.ENGLISH);
    }

    @Test
    void test_simple_localize() {
        this.registry.register("greeting", Locale.FRENCH, new MessageFormat("Bonjour {0}!", Locale.FRENCH));
        this.registry.register("greeting", Locale.ENGLISH, new MessageFormat("Hello {0}!", Locale.ENGLISH));

        assertThat(this.registry.localize("greeting", Locale.FRENCH))
                .isNotNull()
                .extracting(MessageFormat::toPattern)
                .isEqualTo("Bonjour {0}!");

        assertThat(this.registry.localize("greeting", Locale.ENGLISH))
                .isNotNull()
                .extracting(MessageFormat::toPattern)
                .isEqualTo("Hello {0}!");

        assertThat(this.registry.localize("greeting", Locale.CHINESE))
                .isNotNull()
                .extracting(MessageFormat::toPattern)
                .isEqualTo("Hello {0}!");
    }

    @Test
    void test_unregister() {
        this.registry.register("greeting", Locale.FRENCH, new MessageFormat("Bonjour {0}!", Locale.FRENCH));
        this.registry.register("greeting", Locale.ENGLISH, new MessageFormat("Hello {0}!", Locale.ENGLISH));

        assertThat(this.registry.localize("greeting", Locale.ENGLISH)).isNotNull();
        assertThat(this.registry.localize("greeting", Locale.FRENCH)).isNotNull();

        this.registry.unregister("greeting");

        assertThat(this.registry.localize("greeting", Locale.ENGLISH)).isNull();
        assertThat(this.registry.localize("greeting", Locale.FRENCH)).isNull();
    }

    @Test
    void test_illegal_register() {
        this.registry.register("greeting", Locale.FRENCH, new MessageFormat("Bonjour {0}!", Locale.FRENCH));
        assertThatThrownBy(() -> this.registry.register(
                        "greeting", Locale.FRENCH, new MessageFormat("Bonjour {0}!", Locale.FRENCH)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
