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
package com.xpdustry.distributor.api.annotation;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class CompositeAnnotationProcessorTest {

    @Test
    void test_process() {
        final PluginAnnotationProcessor<String> processor1 = $ -> Optional.of("A");
        final PluginAnnotationProcessor<String> processor2 = $ -> Optional.of("B");
        final PluginAnnotationProcessor<String> processor3 = $ -> Optional.empty();
        final PluginAnnotationProcessor<String> processor4 = $ -> Optional.of("D");

        final var composite1 = (CompositeAnnotationProcessor) PluginAnnotationProcessor.compose(processor1, processor2);
        assertThat(composite1.processors()).containsExactly(processor1, processor2);

        final var composite2 =
                (CompositeAnnotationProcessor) PluginAnnotationProcessor.compose(composite1, processor3, processor4);
        assertThat(composite2.processors()).containsExactly(processor1, processor2, processor3, processor4);

        final var instance = new Object();
        final var result = composite2.process(instance);
        assertThat(result).isPresent();
        assertThat(result.get()).containsExactly("A", "B", "D");
    }
}
