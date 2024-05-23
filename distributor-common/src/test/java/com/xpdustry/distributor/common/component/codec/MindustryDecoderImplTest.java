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
package com.xpdustry.distributor.common.component.codec;

import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import org.junit.jupiter.api.Test;

import static com.xpdustry.distributor.api.component.TextComponent.text;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MindustryDecoderImplTest {

    @Test
    void test_decode_plain() {
        final var decoder = new MindustryDecoderImpl();
        final var component = text("Hello, World!");
        assertEquals(component, decoder.decode("Hello, World!"));
    }

    @Test
    void test_decode_color() {
        final var decoder = new MindustryDecoderImpl();
        final var component = text().setContent("Hello, World!")
                .setTextColor(ComponentColor.BLACK)
                .build();
        assertEquals(component, decoder.decode("[#000000]Hello, World!"));
    }

    @Test
    void test_decode_nested() {
        final var decoder = new MindustryDecoderImpl();
        final var component = ListComponent.components()
                .setTextColor(ComponentColor.RED)
                .append(text("Hello, "))
                .append(text("World", ComponentColor.GREEN))
                .append(text("!"))
                .build();
        assertEquals(component, decoder.decode("[#FF0000]Hello, [#00FF00]World[]![]"));
    }
}
