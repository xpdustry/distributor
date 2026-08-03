// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import arc.Events;
import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.event.EventPublisherImpl;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.foundation.util.Priority;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class EventHandlerProcessorTest {

    private EventHandlerProcessor processor;
    private EventPublisher events;

    @BeforeEach
    void setup() {
        MindustryThread.captureMainThread();
        this.events = new EventPublisherImpl();
        this.processor = new EventHandlerProcessor(mock(PluginFacade.class), this.events);
    }

    @AfterEach
    void clear() {
        Events.clear();
        MindustryThread.clear();
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        final var event = new TestEvent("Hello, world!");

        this.processor.process(instance);
        this.events.publish(event);

        assertThat(instance.event).isEqualTo(event);
    }

    @Test
    void test_priority() {
        final var instance = new TestPriority();

        this.processor.process(instance);
        this.events.publish(new TestEvent("Hello, world!"));

        assertThat(instance.numbers).containsExactly(1, 2, 3);
    }

    @Test
    void test_no_parameter() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestNoParameter()));
    }

    @Test
    void test_too_many_parameters() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestTooManyParameters()));
    }

    private static final class TestSimple {
        private @Nullable TestEvent event = null;

        @EventHandler
        private void event(final TestEvent event) {
            this.event = event;
        }
    }

    private static final class TestPriority {
        private final List<Integer> numbers = new ArrayList<>();

        @EventHandler(priority = Priority.LOW)
        private void low(final TestEvent event) {
            this.numbers.add(3);
        }

        @EventHandler(priority = Priority.HIGH)
        private void high(final TestEvent event) {
            this.numbers.add(1);
        }

        @EventHandler
        private void normal(final TestEvent event) {
            this.numbers.add(2);
        }
    }

    private static final class TestNoParameter {
        @EventHandler
        private void event() {}
    }

    private static final class TestTooManyParameters {
        @EventHandler
        private void event(final TestEvent first, final TestEvent second) {}
    }

    private record TestEvent(String message) {}
}
