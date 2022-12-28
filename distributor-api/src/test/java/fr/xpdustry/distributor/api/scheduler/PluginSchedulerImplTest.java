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
package fr.xpdustry.distributor.api.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import arc.Core;
import arc.mock.MockApplication;
import fr.xpdustry.distributor.api.TestPlugin;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class PluginSchedulerImplTest {

    private static final Duration PRECISION = Duration.ofMillis(100L);

    private PluginTimeSource source;
    private PluginSchedulerImpl scheduler;
    private Thread updater;

    @SuppressWarnings("BusyWait")
    @BeforeEach
    void before() {
        Core.app = new MockApplication();
        this.source = PluginTimeSource.standard();
        this.scheduler = new PluginSchedulerImpl(new TestPlugin(), this.source, Runnable::run);
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
        assertThat(this.scheduler.scheduleSync().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> !thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_simple_async_schedule() {
        final var future = new CompletableFuture<Thread>();
        assertThat(this.scheduler.scheduleAsync().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_initial_delay() {
        final var future = new CompletableFuture<Long>();
        final var begin = this.source.getCurrentMillis();
        assertThat(this.scheduler
                        .scheduleSync()
                        .delay(3L, TimeUnit.SECONDS)
                        .execute(() -> future.complete(this.source.getCurrentMillis())))
                .succeedsWithin(Duration.ofSeconds(3L).plus(PRECISION));
        final var end = future.join();
        assertThat(Duration.ofMillis(end - begin)).isCloseTo(Duration.ofSeconds(3L), PRECISION);
    }

    @Test
    void test_repeat_interval() {
        final var counter = new CountDownLatch(3);
        final var longs = new ArrayList<Long>();
        final var future = this.scheduler
                .scheduleSync()
                .repeatInterval(1L, TimeUnit.SECONDS)
                .execute(() -> {
                    longs.add(this.source.getCurrentMillis());
                    Thread.sleep(500L);
                    counter.countDown();
                    return null;
                });

        // Execute the task 3 times
        assertTimeoutPreemptively(Duration.ofSeconds(5L), () -> {
            counter.await();
            future.cancel(false);
        });

        assertThat(Duration.ofMillis(longs.get(1) - longs.get(0))).isCloseTo(Duration.ofMillis(1500L), PRECISION);
        assertThat(Duration.ofMillis(longs.get(2) - longs.get(1))).isCloseTo(Duration.ofMillis(1500L), PRECISION);
    }

    @Test
    void test_repeat_period() {
        final var counter = new CountDownLatch(3);
        final var longs = new ArrayList<Long>();
        final var future = this.scheduler
                .scheduleSync()
                .repeatPeriod(1L, TimeUnit.SECONDS)
                .execute(() -> {
                    longs.add(this.source.getCurrentMillis());
                    Thread.sleep(500L);
                    counter.countDown();
                    return null;
                });

        // Execute the task 3 times
        assertTimeoutPreemptively(Duration.ofSeconds(5L), () -> {
            counter.await();
            future.cancel(false);
        });

        assertThat(Duration.ofMillis(longs.get(1) - longs.get(0))).isCloseTo(Duration.ofSeconds(1L), PRECISION);
        assertThat(Duration.ofMillis(longs.get(2) - longs.get(1))).isCloseTo(Duration.ofSeconds(1L), PRECISION);
    }

    @Test
    void test_recipe() {
        final var steps = new ArrayList<TestRecipeStep<String>>();
        final var task = this.scheduler
                .recipe("initial")
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

    private final class TestRecipeStep<V> {

        private final V value;
        private final String thread = Thread.currentThread().getName();

        private TestRecipeStep(final V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }

        public boolean isAsyncThread() {
            return this.thread.startsWith(PluginSchedulerImplTest.this.scheduler.getBaseWorkerName());
        }

        public boolean isSyncThread() {
            return !this.isAsyncThread();
        }
    }
}
