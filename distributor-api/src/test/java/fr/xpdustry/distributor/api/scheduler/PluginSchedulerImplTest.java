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

import fr.xpdustry.distributor.api.TestPlugin;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class PluginSchedulerImplTest {

    private TestPlugin plugin;
    private PluginSchedulerImpl scheduler;
    private Thread updater;

    @SuppressWarnings("BusyWait")
    @BeforeEach
    void before() {
        this.plugin = new TestPlugin();
        this.scheduler = new PluginSchedulerImpl(plugin, PluginTimeSource.standard(), Runnable::run);
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
        Assertions.assertThat(this.scheduler.schedule().sync().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(Duration.ofSeconds(1));
        Assertions.assertThat(future).isCompletedWithValueMatching(thread -> !thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_simple_async_schedule() {
        final var future = new CompletableFuture<Thread>();
        Assertions.assertThat(this.scheduler.schedule().async().execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(Duration.ofSeconds(1));
        Assertions.assertThat(future).isCompletedWithValueMatching(thread -> thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }
}
