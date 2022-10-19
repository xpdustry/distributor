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

import arc.*;
import fr.xpdustry.distributor.api.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class ArcEventBusTest {

  private ArcEventBus bus;

  @BeforeEach
  void setup() {
    bus = new ArcEventBus();
  }

  @AfterEach
  void clean() {
    Events.clear();
  }

  @Test
  void test_events_event_call() {
    final var listener = new SimpleEventListener();
    final var future3 = new CompletableFuture<Void>();

    bus.register(listener);
    Events.on(TestEvent.class, e -> future3.complete(null));
    Events.fire(new TestEvent());

    assertThat(listener.future).isCompleted();
    assertThat(future3).isCompleted();
  }

  @Test
  void test_bus_event_call() {
    final var listener = new SimpleEventListener();
    final var future3 = new CompletableFuture<Void>();

    bus.register(listener);
    Events.on(TestEvent.class, e -> future3.complete(null));
    bus.post(new TestEvent());

    assertThat(listener.future).isCompleted();
    assertThat(future3).isCompleted();
  }

  @Test
  void test_listener_unregister() {
    final var listener = new SimpleEventListener();
    bus.register(listener);
    bus.post(new TestEvent());
    assertThat(listener.future).isCompleted();

    listener.future = new CompletableFuture<>();
    bus.unregister(listener);
    bus.post(new TestEvent());
    assertThat(listener.future).isNotCompleted();
  }

  @Test
  void test_event_order() {
    final var listener = new EventOrderListener();
    final var future3 = new CompletableFuture<Long>();

    bus.register(listener);
    Events.on(TestEvent.class, e -> future3.complete(System.nanoTime()));
    bus.post(new TestEvent());

    assertThat(listener.future1).isCompleted();
    assertThat(listener.future2).isCompleted();
    assertThat(future3).isCompleted();

    final long time1 = listener.future1.join();
    final long time2 = listener.future2.join();
    final long time3 = future3.join();

    assertThat(time1).isLessThan(time3);
    assertThat(time3).isLessThan(time2);
  }

  private static final class TestEvent {

  }

  private static final class SimpleEventListener {

    private CompletableFuture<Void> future = new CompletableFuture<>();

    @EventHandler
    public void listen(final TestEvent event) {
      future.complete(null);
    }
  }

  private static final class EventOrderListener {

    private final CompletableFuture<Long> future1 = new CompletableFuture<>();
    private final CompletableFuture<Long> future2 = new CompletableFuture<>();

    @EventHandler(priority = Priority.HIGH)
    public void listen1(final TestEvent event) {
      future1.complete(System.nanoTime());
    }

    @EventHandler(priority = Priority.LOW)
    public void listen2(final TestEvent event) {
      future2.complete(System.nanoTime());
    }
  }
}
