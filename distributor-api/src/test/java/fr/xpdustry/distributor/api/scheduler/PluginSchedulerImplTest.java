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
        this.source = PluginTimeSource.standard();
        this.scheduler = new PluginSchedulerImpl(new TestPlugin(), this.source, Runnable::run);
        this.updater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L / 60);
                } catch (final InterruptedException ignored) {
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
        assertThat(this.scheduler.schedule().sync().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> !thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_simple_async_schedule() {
        final var future = new CompletableFuture<Thread>();
        assertThat(this.scheduler.schedule().async().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(PRECISION);
        assertThat(future).isCompletedWithValueMatching(thread -> thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_initial_delay() {
        final var future = new CompletableFuture<Long>();
        final var begin = this.source.getCurrentMillis();
        assertThat(this.scheduler
                        .schedule()
                        .sync()
                        .initialDelay(3L, TimeUnit.SECONDS)
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
                .schedule()
                .sync()
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
                .schedule()
                .sync()
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
}
