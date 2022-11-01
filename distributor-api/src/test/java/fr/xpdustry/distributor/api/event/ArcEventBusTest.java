/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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

import static org.assertj.core.api.Assertions.assertThat;

import arc.Events;
import fr.xpdustry.distributor.api.util.Priority;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArcEventBusTest {

    private ArcEventBus bus;

    @BeforeEach
    void setup() {
        this.bus = new ArcEventBus();
    }

    @AfterEach
    void clean() {
        Events.clear();
    }

    @Test
    void test_events_event_call() {
        final var listener = new SimpleEventListener();
        final var future3 = new CompletableFuture<Void>();

        this.bus.register(listener);
        Events.on(TestEvent.class, e -> future3.complete(null));
        Events.fire(new TestEvent());

        assertThat(listener.future).isCompleted();
        assertThat(future3).isCompleted();
    }

    @Test
    void test_bus_event_call() {
        final var listener = new SimpleEventListener();
        final var future3 = new CompletableFuture<Void>();

        this.bus.register(listener);
        Events.on(TestEvent.class, e -> future3.complete(null));
        this.bus.post(new TestEvent());

        assertThat(listener.future).isCompleted();
        assertThat(future3).isCompleted();
    }

    @Test
    void test_listener_unregister() {
        final var listener = new SimpleEventListener();
        this.bus.register(listener);
        this.bus.post(new TestEvent());
        assertThat(listener.future).isCompleted();

        listener.future = new CompletableFuture<>();
        this.bus.unregister(listener);
        this.bus.post(new TestEvent());
        assertThat(listener.future).isNotCompleted();
    }

    @Test
    void test_event_order() {
        final var listener = new EventOrderListener();
        final var future3 = new CompletableFuture<Long>();

        this.bus.register(listener);
        Events.on(TestEvent.class, e -> future3.complete(System.nanoTime()));
        this.bus.post(new TestEvent());

        assertThat(listener.future1).isCompleted();
        assertThat(listener.future2).isCompleted();
        assertThat(future3).isCompleted();

        final long time1 = listener.future1.join();
        final long time2 = listener.future2.join();
        final long time3 = future3.join();

        assertThat(time1).isLessThan(time3);
        assertThat(time3).isLessThan(time2);
    }

    private static final class TestEvent {}

    private static final class SimpleEventListener {

        private CompletableFuture<Void> future = new CompletableFuture<>();

        @EventHandler
        public void listen(final TestEvent event) {
            this.future.complete(null);
        }
    }

    private static final class EventOrderListener {

        private final CompletableFuture<Long> future1 = new CompletableFuture<>();
        private final CompletableFuture<Long> future2 = new CompletableFuture<>();

        @EventHandler(priority = Priority.HIGH)
        public void listen1(final TestEvent event) {
            this.future1.complete(System.nanoTime());
        }

        @EventHandler(priority = Priority.LOW)
        public void listen2(final TestEvent event) {
            this.future2.complete(System.nanoTime());
        }
    }
}
