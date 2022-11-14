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

import arc.files.Fi;
import fr.xpdustry.distributor.api.TestPlugin;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import mindustry.Vars;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public final class PluginSchedulerTest {

    private TestPlugin plugin;
    private PluginSchedulerImpl scheduler;
    private Thread updater;

    @BeforeAll
    static void setup(@TempDir final Path tempDir) {
        Vars.modDirectory = new Fi(tempDir.toFile());
    }

    @SuppressWarnings("BusyWait")
    @BeforeEach
    void before() {
        this.plugin = new TestPlugin();
        this.scheduler = new PluginSchedulerImpl(this.plugin, 4, PluginTimeSource.standard(), Runnable::run);
        this.updater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L / 60);
                } catch (final InterruptedException ignored) {
                    break;
                }
                this.scheduler.onPluginUpdate(this.plugin);
            }
            this.scheduler.onPluginExit(this.plugin);
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
        Assertions.assertThat(this.scheduler
                        .schedule()
                        .syncExecution()
                        .execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(Duration.ofSeconds(1));
        Assertions.assertThat(future).isCompletedWithValueMatching(thread -> !thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }

    @Test
    void test_simple_async_schedule() {
        final var future = new CompletableFuture<Thread>();
        Assertions.assertThat(this.scheduler
                        .schedule()
                        .asyncExecution()
                        .execute(() -> future.complete(Thread.currentThread())))
                .succeedsWithin(Duration.ofSeconds(1));
        Assertions.assertThat(future).isCompletedWithValueMatching(thread -> thread.getName()
                .startsWith(this.scheduler.getBaseWorkerName()));
    }
}
