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
import mindustry.game.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class TriggerHandlerProcessorTest {

    private TriggerHandlerProcessor processor;
    private EventPublisher events;

    @BeforeEach
    void setup() {
        MindustryThread.captureMainThread();
        this.events = new EventPublisherImpl();
        this.processor = new TriggerHandlerProcessor(mock(PluginFacade.class), this.events);
    }

    @AfterEach
    void clear() {
        Events.clear();
        MindustryThread.clear();
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        this.processor.process(instance);

        this.events.publish(EventType.Trigger.update);
        assertThat(instance.update).isTrue();
        assertThat(instance.draw).isFalse();

        this.events.publish(EventType.Trigger.draw);
        assertThat(instance.draw).isTrue();
    }

    @Test
    void test_priority() {
        final var instance = new TestPriority();

        this.processor.process(instance);
        this.events.publish(EventType.Trigger.update);

        assertThat(instance.numbers).containsExactly(1, 2, 3);
    }

    @Test
    void test_too_many_parameters() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestTooManyParameters()));
    }

    private static final class TestSimple {
        private boolean update = false;
        private boolean draw = false;

        @TriggerHandler(EventType.Trigger.update)
        private void update() {
            this.update = true;
        }

        @TriggerHandler(EventType.Trigger.draw)
        private void draw() {
            this.draw = true;
        }
    }

    private static final class TestPriority {
        private final List<Integer> numbers = new ArrayList<>();

        @TriggerHandler(value = EventType.Trigger.update, priority = Priority.LOW)
        private void low() {
            this.numbers.add(3);
        }

        @TriggerHandler(value = EventType.Trigger.update, priority = Priority.HIGH)
        private void high() {
            this.numbers.add(1);
        }

        @TriggerHandler(EventType.Trigger.update)
        private void normal() {
            this.numbers.add(2);
        }
    }

    private static final class TestTooManyParameters {
        @TriggerHandler(EventType.Trigger.update)
        private void event(final Object first, final Object second) {}
    }
}
