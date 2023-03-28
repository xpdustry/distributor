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
package fr.xpdustry.distributor.core.scheduler;

import arc.Core;
import arc.mock.MockApplication;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.scheduler.Cancellable;
import fr.xpdustry.distributor.api.scheduler.MindustryTimeUnit;
import fr.xpdustry.distributor.api.scheduler.TaskHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

// TODO The tests are so cursed, A better way to measure the time and precision of the scheduler is needed
public final class SimplePluginSchedulerTest {

    // Precision of 0.2 seconds
    private static final Duration PRECISION = Duration.ofMillis(200);
    private static final long PRECISION_TICKS = 12L;

    private MindustryPlugin plugin;
    private TimeSource source;
    private SimplePluginScheduler scheduler;
    private Thread updater;

    @SuppressWarnings("BusyWait")
    @BeforeEach
    void before() {
        Core.app = new MockApplication();
        this.plugin = Mockito.mock(MindustryPlugin.class);
        this.source = TimeSource.standard();
        this.scheduler = new SimplePluginScheduler(this.source, Runnable::run, 4);
        this.updater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L / 60);
                } catch (final InterruptedException e) {
                    break;
                }
                this.scheduler.onPluginUpdate();
            }
            this.scheduler.onPluginExit();
        });
        this.updater.start();
    }

    @AfterEach
    void after() {
        this.updater.interrupt();
    }

    @Test
    void test_simple_sync_schedule() {
        final var future = new CompletableFuture<Thread>();
        assertThat(this.scheduler.scheduleSync(this.plugin).execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> !thread.getName()
                .startsWith(SimplePluginScheduler.DISTRIBUTOR_WORKER_BASE_NAME));
    }

    @Test
    void test_simple_async_schedule() {
        final var future = new CompletableFuture<Thread>();
        assertThat(this.scheduler.scheduleAsync(this.plugin).execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> thread.getName()
                .startsWith(SimplePluginScheduler.DISTRIBUTOR_WORKER_BASE_NAME));
    }

    @Test
    void test_delay() {
        final var future = new CompletableFuture<Long>();
        final var begin = this.source.getCurrentTicks();
        assertThat(this.scheduler
                        .scheduleSync(this.plugin)
                        .delay(1L, MindustryTimeUnit.SECONDS)
                        .execute(() -> future.complete(this.source.getCurrentTicks())))
                .succeedsWithin(Duration.ofSeconds(1L).plus(PRECISION));
        final var end = future.join();
        assertThat(end - begin).isCloseTo(60L, within(PRECISION_TICKS));
    }

    @Test
    void test_interval() {
        final var counter = new CountDownLatch(3);
        final var longs = new ArrayList<Long>();
        final var future = this.scheduler
                .scheduleSync(this.plugin)
                .repeat(500L, MindustryTimeUnit.MILLISECONDS)
                .execute(() -> {
                    longs.add(this.source.getCurrentTicks());
                    try {
                        Thread.sleep(500L);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    counter.countDown();
                });

        // The task should execute 3 times, every second
        assertTimeoutPreemptively(Duration.ofSeconds(4L), () -> {
            counter.await();
            future.cancel(false);
        });

        assertThat(longs.get(1) - longs.get(0)).isCloseTo(60L, within(PRECISION_TICKS));
        assertThat(longs.get(2) - longs.get(1)).isCloseTo(60L, within(PRECISION_TICKS));
    }

    @Test
    void test_cancelling() {
        final var future = new CompletableFuture<Long>();
        final var counter = new AtomicInteger(3);
        final var begin = this.source.getCurrentTicks();
        assertThat(this.scheduler
                        .scheduleSync(this.plugin)
                        .repeat(500L, MindustryTimeUnit.MILLISECONDS)
                        .execute(cancellable -> {
                            if (counter.decrementAndGet() == 0) {
                                cancellable.cancel();
                                future.complete(this.source.getCurrentTicks());
                            }
                        }))
                .failsWithin(Duration.ofSeconds(1L).plus(PRECISION));

        assertTimeoutPreemptively(Duration.ofSeconds(2L), () -> {
            final var end = future.join();
            assertThat(end - begin).isCloseTo(60L, within(PRECISION_TICKS));
        });
    }

    @Test
    void test_recipe() {
        final var steps = new ArrayList<TestRecipeStep<String>>();
        final var task = this.scheduler
                .recipe(this.plugin, "initial")
                .thenAccept(value -> steps.add(new TestRecipeStep<>(value + " accept")))
                .thenApply(value -> {
                    final var newValue = value + " apply";
                    steps.add(new TestRecipeStep<>(newValue));
                    return newValue;
                })
                .thenRun(() -> steps.add(new TestRecipeStep<>("run")))
                .thenAcceptAsync(value -> steps.add(new TestRecipeStep<>(value + " accept async")))
                .thenApplyAsync(value -> {
                    final var newValue = value + " apply async";
                    steps.add(new TestRecipeStep<>(newValue));
                    return newValue;
                })
                .thenRunAsync(() -> steps.add(new TestRecipeStep<>("run async")))
                .execute();

        assertThat(task).succeedsWithin(Duration.ofSeconds(1L)).isEqualTo("initial apply apply async");
        assertThat(steps).size().isEqualTo(6);

        assertThat(steps.get(0))
                .matches(TestRecipeStep::isSyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("initial accept");

        assertThat(steps.get(1))
                .matches(TestRecipeStep::isSyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("initial apply");

        assertThat(steps.get(2))
                .matches(TestRecipeStep::isSyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("run");

        assertThat(steps.get(3))
                .matches(TestRecipeStep::isAsyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("initial apply accept async");

        assertThat(steps.get(4))
                .matches(TestRecipeStep::isAsyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("initial apply apply async");

        assertThat(steps.get(5))
                .matches(TestRecipeStep::isAsyncThread)
                .extracting(TestRecipeStep::getValue)
                .isEqualTo("run async");
    }

    @Test
    void test_task_handler() {
        final var handler = new TestTaskHandler(this.source);
        final var tasks = this.scheduler.parse(this.plugin, handler);

        assertThat(tasks).size().isEqualTo(3);
        assertTimeoutPreemptively(Duration.ofSeconds(1L), () -> handler.latch.await());

        assertThat(handler.longs1.get(1) - handler.longs1.get(0)).isCloseTo(24L, within(PRECISION_TICKS));
        assertThat(handler.longs1.get(2) - handler.longs1.get(1)).isCloseTo(24L, within(PRECISION_TICKS));

        assertThat(handler.longs2.size()).isEqualTo(1);
        assertThat(handler.longs3.size()).isEqualTo(1);
    }

    private static final class TestRecipeStep<V> {

        private final V value;
        private final String thread = Thread.currentThread().getName();

        private TestRecipeStep(final V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }

        public boolean isAsyncThread() {
            return this.thread.startsWith(SimplePluginScheduler.DISTRIBUTOR_WORKER_BASE_NAME);
        }

        public boolean isSyncThread() {
            return !this.isAsyncThread();
        }
    }

    @SuppressWarnings("UnusedMethod")
    private static final class TestTaskHandler {

        private final List<Long> longs1 = new ArrayList<>();
        private final List<Long> longs2 = new ArrayList<>();
        private final List<Long> longs3 = new ArrayList<>();

        private final CountDownLatch latch = new CountDownLatch(1);
        private final TimeSource source;

        private TestTaskHandler(final TimeSource source) {
            this.source = source;
        }

        @TaskHandler(interval = 400L, unit = MindustryTimeUnit.MILLISECONDS)
        public void someTask1() {
            this.longs1.add(this.source.getCurrentTicks());
            if (this.longs1.size() == 3) {
                this.latch.countDown();
            }
        }

        @TaskHandler(interval = 400L, unit = MindustryTimeUnit.MILLISECONDS)
        public void someTask2(final Cancellable cancellable) {
            this.longs2.add(this.source.getCurrentTicks());
            cancellable.cancel();
        }

        @TaskHandler
        public void someTask3() {
            this.longs3.add(this.source.getCurrentTicks());
        }
    }
}
