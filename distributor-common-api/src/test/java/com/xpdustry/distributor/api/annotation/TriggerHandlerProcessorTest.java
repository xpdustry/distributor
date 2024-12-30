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
import mindustry.game.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ManageScheduler.class)
@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class TriggerHandlerProcessorTest {

    private TriggerHandlerProcessor processor;
    private @TestScheduler PluginScheduler scheduler;
    private EventBus events;

    @BeforeEach
    void setup() {
        this.processor = new TriggerHandlerProcessor(new TestPlugin("test"));
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
        this.processor.process(instance);

        this.events.post(EventType.Trigger.update);
        assertThat(instance.event1).isTrue();
        assertThat(instance.event2).isFalse();

        this.events.post(EventType.Trigger.draw);
        assertThat(instance.event2).isTrue();
    }

    @Test
    void test_priority() {
        final var instance = new TestPriority();
        this.processor.process(instance);
        this.events.post(EventType.Trigger.update);
        assertThat(instance.numbers).containsExactly(1, 2, 3);
    }

    @Test
    void test_too_many_parameters() {
        assertThatThrownBy(() -> this.processor.process(new TestTooManyParameters()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static final class TestSimple {

        public boolean event1 = false;
        public boolean event2 = false;

        @TriggerHandler(EventType.Trigger.update)
        public void event() {
            if (this.event1) {
                throw new IllegalStateException("Event1 is true.");
            }
            this.event1 = true;
        }

        @TriggerHandler(EventType.Trigger.draw)
        public void event2() {
            if (this.event2) {
                throw new IllegalStateException("Event2 is true.");
            }
            this.event2 = true;
        }
    }

    private static final class TestPriority {

        public final List<Integer> numbers = new ArrayList<>();

        @TriggerHandler(value = EventType.Trigger.update, priority = Priority.LOW)
        public void event1() {
            this.numbers.add(3);
        }

        @TriggerHandler(value = EventType.Trigger.update, priority = Priority.HIGH)
        public void event2() {
            this.numbers.add(1);
        }

        @TriggerHandler(value = EventType.Trigger.update, priority = Priority.NORMAL)
        public void event3() {
            this.numbers.add(2);
        }
    }

    private static final class TestTooManyParameters {

        @TriggerHandler(EventType.Trigger.update)
        public void event(final Object object1, final Object object2) {}
    }
}
