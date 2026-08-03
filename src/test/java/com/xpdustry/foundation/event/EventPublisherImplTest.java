// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.event;

import arc.Events;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.foundation.util.Priority;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class EventPublisherImplTest {

    private EventPublisher publisher;
    private PluginFacade plugin;

    @BeforeEach
    void setup() {
        MindustryThread.captureMainThread();
        this.publisher = new EventPublisherImpl();
        this.plugin = mock(PluginFacade.class);
    }

    @AfterEach
    void clear() {
        Events.clear();
        MindustryThread.clear();
    }

    @Test
    void test_class_subscribe() {
        final var called = new AtomicBoolean();
        this.publisher.subscribe(this.plugin, TestEvent.class, _ -> called.set(true));

        this.publisher.publish(new TestEvent());

        assertThat(called).isTrue();
    }

    @Test
    void test_enum_subscribe() {
        final var called = new AtomicBoolean();
        this.publisher.subscribe(this.plugin, TestEnum.VALUE, _ -> called.set(true));

        this.publisher.publish(TestEnum.VALUE);

        assertThat(called).isTrue();
    }

    @Test
    void test_arc_event_fire() {
        final var classCalled = new AtomicBoolean();
        final var enumCalled = new AtomicBoolean();
        this.publisher.subscribe(this.plugin, TestEvent.class, _ -> classCalled.set(true));
        this.publisher.subscribe(this.plugin, TestEnum.VALUE, _ -> enumCalled.set(true));

        Events.fire(new TestEvent());
        Events.fire(TestEnum.VALUE);

        assertThat(classCalled).isTrue();
        assertThat(enumCalled).isTrue();
    }

    @Test
    void test_unsubscribe() {
        final var classCalled = new AtomicBoolean();
        final var enumCalled = new AtomicBoolean();
        final var classSubscription =
                this.publisher.subscribe(this.plugin, TestEvent.class, _ -> classCalled.set(true));
        final var enumSubscription = this.publisher.subscribe(this.plugin, TestEnum.VALUE, _ -> enumCalled.set(true));

        classSubscription.unsubscribe();
        enumSubscription.unsubscribe();
        this.publisher.publish(new TestEvent());
        this.publisher.publish(TestEnum.VALUE);

        assertThat(classCalled).isFalse();
        assertThat(enumCalled).isFalse();
        assertThat(EventPublisherImpl.EVENTS_MAP).isEmpty();
    }

    @Test
    void test_class_event_order() {
        final List<Integer> order = new ArrayList<>();
        this.publisher.subscribe(this.plugin, TestEvent.class, Priority.HIGH, _ -> order.add(1));
        Events.on(TestEvent.class, _ -> order.add(2));
        this.publisher.subscribe(this.plugin, TestEvent.class, Priority.LOW, _ -> order.add(3));

        this.publisher.publish(new TestEvent());

        assertThat(order).containsExactly(1, 2, 3);
    }

    @Test
    void test_enum_event_order() {
        final List<Integer> order = new ArrayList<>();
        this.publisher.subscribe(this.plugin, TestEnum.VALUE, Priority.HIGH, _ -> order.add(1));
        Events.run(TestEnum.VALUE, () -> order.add(2));
        this.publisher.subscribe(this.plugin, TestEnum.VALUE, Priority.LOW, _ -> order.add(3));

        this.publisher.publish(TestEnum.VALUE);

        assertThat(order).containsExactly(1, 2, 3);
    }

    private enum TestEnum {
        VALUE
    }

    private static final class TestEvent {}
}
