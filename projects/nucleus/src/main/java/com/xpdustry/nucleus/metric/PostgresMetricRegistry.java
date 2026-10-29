// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import arc.Core;
import com.google.gson.Gson;
import com.xpdustry.foundation.annotation.ScheduledTaskHandler;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import com.xpdustry.nucleus.concurrent.Async;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.database.PostgresDatabase;
import com.xpdustry.nucleus.dependency.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public final class PostgresMetricRegistry implements MetricRegistry, PluginListener {

    private final PluginLogger logger;

    private final List<MetricCollectorWithAsync> collectors = new CopyOnWriteArrayList<>();
    private final ConfigManager config;
    private final Executor executor;
    private final Gson gson;
    private final PostgresDatabase database;

    @Inject
    public PostgresMetricRegistry(
            final ConfigManager config,
            final Executor executor,
            final Gson gson,
            final PostgresDatabase database,
            final PluginLogger logger) {
        this.logger = logger;
        this.config = config;
        this.executor = executor;
        this.gson = gson;
        this.database = database;
    }

    @Override
    public void register(final MetricCollector collector, final boolean async) {
        this.collectors.add(new MetricCollectorWithAsync(collector, async));
    }

    // TODO Unslop Later
    @ScheduledTaskHandler(initialDelay = 5, delay = 5, unit = MindustryTimeUnit.SECONDS)
    @Async
    private void onMetricsCollect() {
        if (!this.config.get(ConfigPropertyKey.METRICS_ENABLED)) {
            return;
        }
        final var server = this.config.get(ConfigPropertyKey.SERVER_NAME);
        final var now = Instant.now();
        final var sink = new BufferedMetricSink();
        CompletableFuture.allOf(this.collectors.stream()
                        .map(pair -> CompletableFuture.runAsync(
                                        () -> pair.collector.flush(sink), pair.async ? this.executor : Core.app::post)
                                .orTimeout(5, TimeUnit.SECONDS)
                                .exceptionally(e -> {
                                    this.logger.error("Failed to collect metrics from {}", pair.collector, e);
                                    return null;
                                }))
                        .toArray(CompletableFuture[]::new))
                .join();

        try {
            sink.seal();
            this.database.withTransaction(handle -> handle.prepareStatement("""
                        INSERT INTO "metric" ("server_id", "measurement", "measured_at", "tags", "value")
                        VALUES (?, ?, ?, ?::jsonb, ?)
                        """)
                    .batch(
                            sink.buffer,
                            (binder, sample) -> binder.bind(server)
                                    .bind(sample.name)
                                    .bind(now)
                                    .bind(this.gson.toJson(sample.labels))
                                    .bind(sample.value))
                    .executeUpdate());
        } catch (final Exception e) {
            this.logger.error("Failed to collect and publish metrics", e);
        }
    }

    record MetricSample(String name, double value, Map<String, String> labels) {
        MetricSample {
            labels = Map.copyOf(labels);
        }
    }

    private record MetricCollectorWithAsync(MetricCollector collector, boolean async) {}

    private static final class BufferedMetricSink implements MetricSink {

        private final List<MetricSample> buffer = new ArrayList<>();
        private boolean sealed = false;

        @Override
        public synchronized void sample(final String name, final Number value, final Map<String, String> labels) {
            if (this.sealed) throw new IllegalStateException("The sink is sealed");
            this.buffer.add(new MetricSample(name, value.doubleValue(), labels));
        }

        private synchronized void seal() {
            this.sealed = true;
        }
    }
}
