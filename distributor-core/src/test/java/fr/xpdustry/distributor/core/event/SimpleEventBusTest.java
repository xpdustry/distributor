/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.core.event;

import arc.Events;
import fr.xpdustry.distributor.api.event.EventHandler;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.util.Priority;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

public final class SimpleEventBusTest {

    @Mock
    private MindustryPlugin plugin;

    private SimpleEventBus bus;

    @BeforeEach
    void setup() {
        this.bus = new SimpleEventBus();
    }

    @AfterEach
    void clean() {
        Events.clear();
    }

    @Test
    void test_class_subscribe() {
        final var subscriber = new ClassEventSubscriber<TestEvent1>();
        this.bus.subscribe(TestEvent1.class, this.plugin, subscriber);
        this.bus.post(new TestEvent1());
        assertThat(subscriber.hasBeenTriggered()).isTrue();
    }

    @Test
    void test_enum_subscribe() {
        final var subscriber = new EnumEventSubscriber();
        this.bus.subscribe(TestEnum.VALUE, this.plugin, subscriber);
        this.bus.post(TestEnum.VALUE);
        assertThat(subscriber.hasBeenTriggered()).isTrue();
    }

    @Test
    void test_arc_event_fire() {
        final var subscriber1 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber2 = new EnumEventSubscriber();

        Events.on(TestEvent1.class, subscriber1::accept);
        Events.run(TestEnum.VALUE, subscriber2);
        Events.fire(new TestEvent1());
        Events.fire(TestEnum.VALUE);

        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();
    }

    @Test
    void test_listener_unregister() {
        final var subscriber1 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber2 = new EnumEventSubscriber();

        final var subscription1 = this.bus.subscribe(TestEvent1.class, this.plugin, subscriber1);
        final var subscription2 = this.bus.subscribe(TestEnum.VALUE, this.plugin, subscriber2);

        this.bus.post(new TestEvent1());
        this.bus.post(TestEnum.VALUE);
        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();

        subscription1.unsubscribe();
        subscription2.unsubscribe();
        subscriber1.reset();
        subscriber2.reset();

        this.bus.post(new TestEvent1());
        this.bus.post(TestEnum.VALUE);
        assertThat(subscriber1.hasBeenTriggered()).isFalse();
        assertThat(subscriber2.hasBeenTriggered()).isFalse();

        assertThat(this.bus.events.isEmpty()).isTrue();
    }

    @Test
    void test_class_event_order() {
        final var subscriber1 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber2 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber3 = new ClassEventSubscriber<TestEvent1>();

        this.bus.subscribe(TestEvent1.class, Priority.HIGH, this.plugin, subscriber1);
        Events.on(TestEvent1.class, subscriber2::accept);
        this.bus.subscribe(TestEvent1.class, Priority.LOW, this.plugin, subscriber3);

        this.bus.post(new TestEvent1());

        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();
        assertThat(subscriber3.hasBeenTriggered()).isTrue();

        assertThat(subscriber1.triggerTime).isLessThan(subscriber2.triggerTime);
        assertThat(subscriber2.triggerTime).isLessThan(subscriber3.triggerTime);
    }

    @Test
    void test_enum_event_order() {
        final var subscriber1 = new EnumEventSubscriber();
        final var subscriber2 = new EnumEventSubscriber();
        final var subscriber3 = new EnumEventSubscriber();

        this.bus.subscribe(TestEnum.VALUE, Priority.HIGH, this.plugin, subscriber1);
        Events.run(TestEnum.VALUE, subscriber2);
        this.bus.subscribe(TestEnum.VALUE, Priority.LOW, this.plugin, subscriber3);

        this.bus.post(TestEnum.VALUE);

        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();
        assertThat(subscriber3.hasBeenTriggered()).isTrue();

        assertThat(subscriber1.triggerTime).isLessThan(subscriber2.triggerTime);
        assertThat(subscriber2.triggerTime).isLessThan(subscriber3.triggerTime);
    }

    @Test
    void test_annotated_subscriber() {
        final var listener = new AnnotatedEventListener();
        final var subscription = this.bus.parse(this.plugin, listener);

        assertThat(listener.hasBeenTriggered1()).isFalse();
        assertThat(listener.hasBeenTriggered2()).isFalse();

        this.bus.post(new TestEvent1());

        assertThat(listener.hasBeenTriggered1()).isTrue();
        assertThat(listener.hasBeenTriggered2()).isFalse();

        this.bus.post(new TestEvent2());

        assertThat(listener.hasBeenTriggered1()).isTrue();
        assertThat(listener.hasBeenTriggered2()).isTrue();

        listener.reset();
        subscription.unsubscribe();

        this.bus.post(new TestEvent1());
        this.bus.post(new TestEvent2());

        assertThat(listener.hasBeenTriggered1()).isFalse();
        assertThat(listener.hasBeenTriggered2()).isFalse();
    }

    @Test
    void test_super_class_post() {
        final var subscriber = new ClassEventSubscriber<TestEvent3>();
        this.bus.subscribe(TestEvent3.class, this.plugin, subscriber);
        this.bus.post(new TestEvent4());
        // SimpleEventBus does not support super class posting
        assertThat(subscriber.hasBeenTriggered()).isFalse();
        // So we have to do it manually
        this.bus.post(TestEvent3.class, new TestEvent4());
        assertThat(subscriber.hasBeenTriggered()).isTrue();
    }

    private enum TestEnum {
        VALUE
    }

    private static final class TestEvent1 {}

    private static final class TestEvent2 {}

    private static class TestEvent3 {}

    private static class TestEvent4 extends TestEvent3 {}

    private static final class ClassEventSubscriber<E> implements Consumer<E> {

        private long triggerTime = -1;

        @Override
        public void accept(final E event) {
            if (this.hasBeenTriggered()) {
                throw new IllegalStateException("The subscriber has been triggered twice.");
            }
            this.triggerTime = System.nanoTime();
        }

        private boolean hasBeenTriggered() {
            return this.triggerTime != -1;
        }

        private void reset() {
            if (!this.hasBeenTriggered()) {
                throw new IllegalStateException("Tried to reset while not triggered.");
            }
            this.triggerTime = -1;
        }
    }

    private static final class EnumEventSubscriber implements Runnable {

        private long triggerTime = -1;

        @Override
        public void run() {
            if (this.hasBeenTriggered()) {
                throw new IllegalStateException("The subscriber has been triggered twice.");
            }
            this.triggerTime = System.nanoTime();
        }

        private boolean hasBeenTriggered() {
            return this.triggerTime != -1;
        }

        private void reset() {
            if (!this.hasBeenTriggered()) {
                throw new IllegalStateException("Tried to reset while not triggered.");
            }
            this.triggerTime = -1;
        }
    }

    @SuppressWarnings("unused")
    private static final class AnnotatedEventListener {

        private long triggerTime1 = -1;
        private long triggerTime2 = -1;

        @EventHandler
        public void listenTo1(final TestEvent1 event) {
            if (this.hasBeenTriggered1()) {
                throw new IllegalStateException("The subscriber has been triggered twice.");
            }
            this.triggerTime1 = System.nanoTime();
        }

        @EventHandler
        public void listenTo2(final TestEvent2 event) {
            if (this.hasBeenTriggered2()) {
                throw new IllegalStateException("The subscriber has been triggered twice.");
            }
            this.triggerTime2 = System.nanoTime();
        }

        private boolean hasBeenTriggered1() {
            return this.triggerTime1 != -1;
        }

        private boolean hasBeenTriggered2() {
            return this.triggerTime2 != -1;
        }

        private void reset() {
            if (!this.hasBeenTriggered1()) {
                throw new IllegalStateException("Tried to reset while not triggered.");
            }
            if (!this.hasBeenTriggered2()) {
                throw new IllegalStateException("Tried to reset while not triggered.");
            }
            this.triggerTime1 = -1;
            this.triggerTime2 = -1;
        }
    }
}
