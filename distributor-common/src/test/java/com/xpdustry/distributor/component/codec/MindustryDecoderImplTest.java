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
package com.xpdustry.distributor.component.codec;

import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import org.junit.jupiter.api.Test;

import static com.xpdustry.distributor.api.component.TextComponent.text;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MindustryDecoderImplTest {

    @Test
    void test_decode() {
        final var codec = new MindustryDecoderImpl();
        final var context = MetadataContainer.empty();

        final var component1 = text("Hello, World!");
        assertEquals(component1, codec.decode("Hello, World!", context));

        final var component2 = text().setContent("Hello, World!")
                .setTextColor(ComponentColor.BLACK)
                .build();
        assertEquals(component2, codec.decode("[#000000]Hello, World!", context));

        // Try nested components
        final var component3 = ListComponent.components()
                .setTextColor(ComponentColor.RED)
                .append(text("Hello, "))
                .append(text("World", ComponentColor.GREEN))
                .append(text("!"))
                .build();
        assertEquals(component3, codec.decode("[#FF0000]Hello, [#00FF00]World[]![]", context));
    }
}
