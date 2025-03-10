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

import arc.Events;
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.test.ManageScheduler;
import com.xpdustry.distributor.api.test.TestPlugin;
import com.xpdustry.distributor.api.test.TestScheduler;
import com.xpdustry.distributor.api.util.Priority;
import com.xpdustry.distributor.common.event.EventBusImpl;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ManageScheduler.class)
@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class EventHandlerProcessorTest {

    private EventHandlerProcessor processor;
    private @TestScheduler PluginScheduler scheduler;
    private EventBus events;

    @BeforeEach
    void setup() {
        this.processor = new EventHandlerProcessor(new TestPlugin("test"));
        this.events = new EventBusImpl();
        final var distributor = Mockito.mock(Distributor.class);
        Mockito.when(distributor.getPluginScheduler()).thenReturn(this.scheduler);
        Mockito.when(distributor.getEventBus()).thenReturn(this.events);
        Distributor.set(distributor);
    }

    @AfterEach
    void clear() {
        Distributor.set(null);
        Events.clear(); // TODO Add dedicated injection annotation for EventBus
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        final var event = new TestEvent("Hello, world!");
        this.processor.process(instance);
        this.events.post(event);
        assertThat(instance.event).isEqualTo(event);
    }

    @Test
    void test_priority() {
        final var instance = new TestPriority();
        final var event = new TestEvent("Hello, world!");
        this.processor.process(instance);
        this.events.post(event);
        assertThat(instance.numbers).containsExactly(1, 2, 3);
    }

    @Test
    void test_no_parameter() {
        assertThatThrownBy(() -> this.processor.process(new TestNoParameter()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_too_many_parameters() {
        assertThatThrownBy(() -> this.processor.process(new TestTooManyParameters()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static final class TestSimple {

        public @Nullable TestEvent event = null;

        @EventHandler
        public void event(final TestEvent event) {
            if (this.event != null) {
                throw new IllegalStateException("Event is not null.");
            }
            this.event = event;
        }
    }

    private static final class TestPriority {

        public final List<Integer> numbers = new ArrayList<>();

        @EventHandler(priority = Priority.LOW)
        public void event1(final TestEvent event) {
            this.numbers.add(3);
        }

        @EventHandler(priority = Priority.HIGH)
        public void event2(final TestEvent event) {
            this.numbers.add(1);
        }

        @EventHandler(priority = Priority.NORMAL)
        public void event3(final TestEvent event) {
            this.numbers.add(2);
        }
    }

    private static final class TestNoParameter {

        @EventHandler
        public void event() {}
    }

    private static final class TestTooManyParameters {

        @EventHandler
        public void event(final TestEvent event1, final TestEvent event2) {}
    }

    private record TestEvent(String message) {}
}
