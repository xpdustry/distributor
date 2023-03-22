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
package fr.xpdustry.distributor.api.event;

import arc.Events;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.util.Priority;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

public final class MoreEventsTest {

    @Mock
    private MindustryPlugin plugin;

    @AfterEach
    void clean() {
        Events.clear();
    }

    @Test
    void test_class_subscribe() {
        final var subscriber = new ClassEventSubscriber<TestEvent1>();
        MoreEvents.subscribe(TestEvent1.class, this.plugin, subscriber);
        MoreEvents.post(new TestEvent1());
        assertThat(subscriber.hasBeenTriggered()).isTrue();
    }

    @Test
    void test_enum_subscribe() {
        final var subscriber = new EnumEventSubscriber();
        MoreEvents.subscribe(TestEnum.VALUE, this.plugin, subscriber);
        MoreEvents.post(TestEnum.VALUE);
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

        final var subscription1 = MoreEvents.subscribe(TestEvent1.class, this.plugin, subscriber1);
        final var subscription2 = MoreEvents.subscribe(TestEnum.VALUE, this.plugin, subscriber2);

        MoreEvents.post(new TestEvent1());
        MoreEvents.post(TestEnum.VALUE);
        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();

        subscription1.unsubscribe();
        subscription2.unsubscribe();
        subscriber1.reset();
        subscriber2.reset();

        MoreEvents.post(new TestEvent1());
        MoreEvents.post(TestEnum.VALUE);
        assertThat(subscriber1.hasBeenTriggered()).isFalse();
        assertThat(subscriber2.hasBeenTriggered()).isFalse();

        assertThat(MoreEvents.events.isEmpty()).isTrue();
    }

    @Test
    void test_class_event_order() {
        final var subscriber1 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber2 = new ClassEventSubscriber<TestEvent1>();
        final var subscriber3 = new ClassEventSubscriber<TestEvent1>();

        MoreEvents.subscribe(TestEvent1.class, Priority.HIGH, this.plugin, subscriber1);
        Events.on(TestEvent1.class, subscriber2::accept);
        MoreEvents.subscribe(TestEvent1.class, Priority.LOW, this.plugin, subscriber3);

        MoreEvents.post(new TestEvent1());

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

        MoreEvents.subscribe(TestEnum.VALUE, Priority.HIGH, this.plugin, subscriber1);
        Events.run(TestEnum.VALUE, subscriber2);
        MoreEvents.subscribe(TestEnum.VALUE, Priority.LOW, this.plugin, subscriber3);

        MoreEvents.post(TestEnum.VALUE);

        assertThat(subscriber1.hasBeenTriggered()).isTrue();
        assertThat(subscriber2.hasBeenTriggered()).isTrue();
        assertThat(subscriber3.hasBeenTriggered()).isTrue();

        assertThat(subscriber1.triggerTime).isLessThan(subscriber2.triggerTime);
        assertThat(subscriber2.triggerTime).isLessThan(subscriber3.triggerTime);
    }

    @Test
    void test_annotated_subscriber() {
        final var listener = new AnnotatedEventListener();

        MoreEvents.parse(this.plugin, listener);

        assertThat(listener.future1).isNotCompleted();
        assertThat(listener.future2).isNotCompleted();

        MoreEvents.post(new TestEvent1());

        assertThat(listener.future1).isCompleted();
        assertThat(listener.future2).isNotCompleted();

        MoreEvents.post(new TestEvent2());

        assertThat(listener.future1).isCompleted();
        assertThat(listener.future2).isCompleted();
    }

    private enum TestEnum {
        VALUE
    }

    private static final class TestEvent1 {}

    private static final class TestEvent2 {}

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

        private final CompletableFuture<TestEvent1> future1 = new CompletableFuture<>();
        private final CompletableFuture<TestEvent2> future2 = new CompletableFuture<>();

        @EventHandler
        public void listenTo1(final TestEvent1 event) {
            this.future1.complete(event);
        }

        @EventHandler
        public void listenTo2(final TestEvent2 event) {
            this.future2.complete(event);
        }
    }
}
