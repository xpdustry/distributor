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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.Cancellable;
import com.xpdustry.distributor.api.scheduler.MindustryTimeUnit;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.test.ManageScheduler;
import com.xpdustry.distributor.api.test.TestScheduler;
import com.xpdustry.distributor.common.scheduler.PluginSchedulerImpl;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ManageScheduler.class)
@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class TaskHandlerProcessorTest {

    private static final Duration PRECISION = Duration.ofMillis(100);

    private @Mock MindustryPlugin plugin;
    private @TestScheduler PluginScheduler scheduler;

    @BeforeEach
    void setup() {
        final var distributor = Mockito.mock(Distributor.class);
        Mockito.when(distributor.getPluginScheduler()).thenReturn(scheduler);
        DistributorProvider.set(distributor);
    }

    @AfterEach
    void clear() {
        DistributorProvider.clear();
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        new TaskHandlerProcessor(plugin).process(instance);
        assertThat(instance.future).succeedsWithin(Duration.ofSeconds(1L).plus(PRECISION));
    }

    @Test
    void test_cancellable_parameter() throws InterruptedException {
        final var instance = new TestCancellableParameter();
        new TaskHandlerProcessor(plugin).process(instance);
        Thread.sleep(500L);
        assertThat(instance.counter).hasValue(3);
    }

    @Test
    void test_invalid_parameter() {
        assertThatThrownBy(() -> new TaskHandlerProcessor(plugin).process(new TestInvalidParameter()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_too_many_parameters() {
        assertThatThrownBy(() -> new TaskHandlerProcessor(plugin).process(new TestTooManyParameters()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_async() {
        final var instance = new TestAsync();
        new TaskHandlerProcessor(plugin).process(instance);
        assertThat(instance.future)
                .succeedsWithin(Duration.ofSeconds(1L).plus(PRECISION), InstanceOfAssertFactories.STRING)
                .startsWith(PluginSchedulerImpl.DISTRIBUTOR_WORKER_BASE_NAME);
    }

    private static final class TestSimple {

        public final CompletableFuture<Boolean> future = new CompletableFuture<>();

        @TaskHandler(delay = 1, unit = MindustryTimeUnit.SECONDS)
        public void task() {
            future.complete(true);
        }
    }

    private static final class TestCancellableParameter {

        private final AtomicInteger counter = new AtomicInteger(0);

        @TaskHandler(interval = 100, unit = MindustryTimeUnit.MILLISECONDS)
        public void task(final Cancellable cancellable) {
            if (counter.incrementAndGet() == 3) {
                cancellable.cancel();
            }
        }
    }

    private static final class TestInvalidParameter {

        @TaskHandler
        public void task(final String invalid) {}
    }

    private static final class TestTooManyParameters {

        @TaskHandler
        public void task(final Cancellable cancellable1, final Cancellable cancellable2) {}
    }

    private static final class TestAsync {

        public final CompletableFuture<String> future = new CompletableFuture<>();

        @TaskHandler
        @Async
        public void task() {
            future.complete(Thread.currentThread().getName());
        }
    }
}
