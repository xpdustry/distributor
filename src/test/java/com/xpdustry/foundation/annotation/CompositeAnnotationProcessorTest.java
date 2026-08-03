// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class CompositeAnnotationProcessorTest {

    @Test
    void test_process() {
        final PluginAnnotationProcessor<String> processor1 = _ -> Optional.of("A");
        final PluginAnnotationProcessor<String> processor2 = _ -> Optional.of("B");
        final PluginAnnotationProcessor<String> processor3 = _ -> Optional.empty();
        final PluginAnnotationProcessor<String> processor4 = _ -> Optional.of("D");

        final var composite1 = (CompositeAnnotationProcessor) PluginAnnotationProcessor.compose(processor1, processor2);
        assertThat(composite1.processors()).containsExactly(processor1, processor2);

        final var composite2 =
                (CompositeAnnotationProcessor) PluginAnnotationProcessor.compose(composite1, processor3, processor4);
        assertThat(composite2.processors()).containsExactly(processor1, processor2, processor3, processor4);
        assertThat(composite2.process(new Object()))
                .hasValueSatisfying(result -> assertThat(result).containsExactly("A", "B", "D"));
    }
}
