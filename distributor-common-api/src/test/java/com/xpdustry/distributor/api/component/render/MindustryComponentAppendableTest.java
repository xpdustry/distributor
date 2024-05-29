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
package com.xpdustry.distributor.api.component.render;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.translation.BundleTranslationSource;
import com.xpdustry.distributor.api.translation.Translation;
import com.xpdustry.distributor.api.translation.TranslationArguments;
import com.xpdustry.distributor.api.translation.TranslationSourceRegistry;
import com.xpdustry.distributor.common.component.render.StandardComponentRendererProvider;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.xpdustry.distributor.api.component.TextComponent.text;
import static com.xpdustry.distributor.api.component.TranslatableComponent.translatable;
import static com.xpdustry.distributor.api.component.ValueComponent.value;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MindustryComponentAppendableTest {

    @BeforeAll
    static void setup() {
        final var translator = BundleTranslationSource.create(Locale.ENGLISH);
        translator.register("greeting", Locale.FRENCH, Translation.text("Bonjour"));
        translator.register("greeting", Locale.ENGLISH, Translation.text("Hello"));
        translator.register(
                "describe-number",
                Locale.ENGLISH,
                Translation.format("The [number] is {0,choice,0#zero|1#one}", Locale.ENGLISH));
        final var distributor = Mockito.mock(Distributor.class);
        final var global = TranslationSourceRegistry.create();
        global.register(translator);
        Mockito.when(distributor.getGlobalTranslationSource()).thenReturn(global);
        DistributorProvider.set(distributor);
    }

    @AfterAll
    static void cleanup() {
        DistributorProvider.clear();
    }

    @Test
    void test_append_text_simple() {
        final var appendable = createAppendable();
        appendable.append("Hello, World!");
        assertEquals("Hello, World!", appendable.toString());
    }

    @Test
    void test_append_escaped_text() {
        final var appendable = createAppendable();
        appendable.append("Hello, [World]!");
        assertEquals("Hello, [[World]!", appendable.toString());
    }

    @Test
    void test_append_component_simple() {
        final var appendable = createAppendable();
        appendable.append(text("Hello, World!"));
        assertEquals("Hello, World!", appendable.toString());
    }

    @Test
    void test_append_component_colored() {
        final var appendable = createAppendable();
        appendable.append(text("Hello, World!", ComponentColor.RED));
        assertEquals("[#FF0000]Hello, World![]", appendable.toString());
    }

    @Test
    void test_append_nested_components() {
        final var appendable = createAppendable();
        appendable.append(ListComponent.components()
                .setTextColor(ComponentColor.RED)
                .append(text("Hello, "))
                .append(text("World", ComponentColor.GREEN))
                .append(text("!"))
                .build());
        assertEquals("[#FF0000]Hello, [#00FF00]World[]![]", appendable.toString());
    }

    @Test
    void test_append_component_duplicate_colors() {
        final var appendable = createAppendable();
        appendable.append(ListComponent.components()
                .setTextColor(ComponentColor.RED)
                .append(text("Hello, ", ComponentColor.RED))
                .append(text("World", ComponentColor.RED))
                .append(text("!", ComponentColor.RED))
                .build());
        assertEquals("[#FF0000]Hello, World![]", appendable.toString());
    }

    @Test
    void test_append_translatable_simple() {
        final var component = translatable("greeting", ComponentColor.RED);
        assertEquals(
                "[#FF0000]Bonjour[]",
                createAppendable(Locale.FRENCH).append(component).toString());
        assertEquals(
                "[#FF0000]Hello[]",
                createAppendable(Locale.ENGLISH).append(component).toString());
    }

    @Test
    void test_translatable_arguments() {
        final var component1 = translatable(
                "describe-number", TranslationArguments.array(value(0, ComponentColor.YELLOW)), ComponentColor.RED);
        assertEquals(
                "[#FF0000]The [[number] is [#FFFF00]zero[][]",
                createAppendable().append(component1).toString());

        final var component2 = translatable("describe-number", TranslationArguments.array(0), ComponentColor.RED);
        assertEquals(
                "[#FF0000]The [[number] is zero[]",
                createAppendable().append(component2).toString());

        final var component3 = translatable("describe-number", TranslationArguments.array("0"), ComponentColor.RED);
        assertEquals(
                "[#FF0000]The [[number] is 0[]",
                createAppendable().append(component3).toString());
    }

    private MindustryComponentAppendable createAppendable(final Locale locale) {
        return new MindustryComponentAppendable(
                MetadataContainer.builder()
                        .putConstant(StandardKeys.LOCALE, locale)
                        .build(),
                new StandardComponentRendererProvider());
    }

    private MindustryComponentAppendable createAppendable() {
        return createAppendable(Locale.ENGLISH);
    }
}
