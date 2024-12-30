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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import org.junit.jupiter.api.Test;

import static com.xpdustry.distributor.api.component.TextComponent.text;
import static org.assertj.core.api.Assertions.assertThat;

public final class MindustryDecoderImplTest {

    @Test
    void test_decode_plain() {
        this.assertStringDecodesTo("Hello, World!", text("Hello, World!"));
    }

    @Test
    void test_decode_color() {
        this.assertStringDecodesTo("[#FF0000]Hello, World!", text("Hello, World!", ComponentColor.RED));
    }

    @Test
    void test_escape_color() {
        this.assertStringDecodesTo("[[#FF0000]Hello, World!", text("[#FF0000]Hello, World!"));
    }

    @Test
    void test_escape_left_brackets() {
        this.assertStringDecodesTo("A[[[[[[[[Hello, World![[[[[", text("A[[[[Hello, World![[["));
        this.assertStringDecodesTo("[[[[[[[[Hello, World![[[[[", text("[[[[Hello, World![[["));
        this.assertStringDecodesTo("[[[[[[[[Hello, World![[[[[A", text("[[[[Hello, World![[[A"));
    }

    @Test
    void test_escape_right_brackets() {
        this.assertStringDecodesTo("A]]]]]]]]Hello, World!]]]]]", text("A]]]]]]]]Hello, World!]]]]]"));
        this.assertStringDecodesTo("]]]]]]]]Hello, World!]]]]]", text("]]]]]]]]Hello, World!]]]]]"));
        this.assertStringDecodesTo("]]]]]]]]Hello, World!]]]]]A", text("]]]]]]]]Hello, World!]]]]]A"));
    }

    @Test
    void test_decode_nested_simple() {
        this.assertStringDecodesTo(
                "[#ff0000]Hello, [#00FF00]World[]![]",
                ListComponent.components(
                        text("Hello, ", ComponentColor.RED),
                        text("World", ComponentColor.GREEN),
                        text("!", ComponentColor.RED)));
    }

    @Test
    void test_decode_nested_complex() {
        this.assertStringDecodesTo(
                "[#ff0000]A[#00FF00][[B][]C[][][D][][#0000FF]E",
                ListComponent.components(
                        text("A", ComponentColor.RED),
                        text("[B]", ComponentColor.GREEN),
                        text("C", ComponentColor.RED),
                        text("[D]"),
                        text("E", ComponentColor.BLUE)));
    }

    @Test
    void test_decode_color_with_alpha() {
        this.assertStringDecodesTo("[#11223344]Hello, World!", text("Hello, World!", ComponentColor.rgb(0x112233)));
    }

    private void assertStringDecodesTo(final String input, final Component expected) {
        assertThat(MindustryDecoderImpl.INSTANCE.decode(input)).isEqualTo(expected);
    }
}
