// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import com.google.gson.Gson;
import com.xpdustry.foundation.annotation.ScheduledTaskHandler;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryTask;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import com.xpdustry.nucleus.concurrent.Async;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.database.PostgresDatabase;
import com.xpdustry.nucleus.dependency.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

public final class Messenger implements PluginListener {

    private final PluginLogger logger;
    private static final Duration DISCOVERY_TTL = Duration.ofSeconds(45);
    private static final String CHANNEL_NAME = "nucleus_message_v1";

    private final ConfigManager config;
    private final Executor executor;
    private final PostgresDatabase database;
    private final Gson gson = new Gson();
    private final ServerInfoProvider info;
    private final Map<String, List<MessageSubscriber<?>>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, DiscoveredServer> servers = new ConcurrentHashMap<>();

    @Inject
    public Messenger(
            final ConfigManager config,
            final Executor executor,
            final PostgresDatabase database,
            final ServerInfoProvider info,
            final PluginLogger logger) {
        this.logger = logger;
        this.config = config;
        this.executor = executor;
        this.database = database;
        this.info = info;
        this.subscribe(DiscoveryMessage.class, (sender, message) -> {
            if (message.info() == null) {
                this.servers.remove(sender);
                this.logger.debug("Server {} left the messenger network", sender);
            } else if (this.servers.put(sender, new DiscoveredServer(message.info(), Instant.now())) == null) {
                this.logger.debug("Server {} added to the messenger network", sender);
            }
        });
    }

    /// Returns the servers that sent a heartbeat recently, this one included.
    public Map<String, DiscoveredServer> servers() {
        return Collections.unmodifiableMap(this.servers);
    }

    public <M extends Message> void subscribe(final Class<M> type, final MessageSubscriber<M> subscriber) {
        this.subscribers
                .computeIfAbsent(type.getName(), _ -> new CopyOnWriteArrayList<>())
                .add(subscriber);
    }

    // TODO Unslop Later
    public <M extends Message> void publish(final M message) {
        final var payload = new StringBuilder();
        try {
            payload.append(message.getClass().getName());
            payload.append('|');
            payload.append(this.config.get(ConfigPropertyKey.SERVER_NAME));
            payload.append('|');
            payload.append(this.gson.toJson(message));
        } catch (final Exception e) {
            this.logger.error(
                    "Failed to serialize a message of type {}",
                    message.getClass().getName(),
                    e);
            return;
        }
        if (payload.length() > 2000) {
            this.logger.error(
                    "Failed to publish message of type {}, payload is too large, got {}",
                    message.getClass().getName(),
                    payload.length());
            return;
        }
        this.database.withTransaction(handle -> handle.prepareStatement("SELECT pg_notify(?, ?)")
                .bind(CHANNEL_NAME)
                .bind(payload.toString())
                .executeSelect(_ -> Boolean.TRUE));
    }

    @Override
    public void onExit() {
        try {
            this.publish(new DiscoveryMessage(null));
        } catch (final Exception e) {
            this.logger.error("Failed to notify other servers of the exit", e);
        }
    }

    @ScheduledTaskHandler(initialDelay = 0, delay = 15, unit = MindustryTimeUnit.SECONDS)
    @Async
    void onDiscoveryHeartbeat() {
        final var now = Instant.now();
        this.servers
                .values()
                .removeIf(server -> server.seenAt().plus(DISCOVERY_TTL).isBefore(now));
        this.publish(new DiscoveryMessage(this.info.get()));
    }

    // TODO Unslop Later
    @ScheduledTaskHandler(initialDelay = 0, delay = 5, unit = MindustryTimeUnit.SECONDS)
    @Async
    void onNotificationPoll(final MindustryTask task) {
        try (final var connection = this.database.newOrphanConnection()) {
            connection.setAutoCommit(true);
            try (final var statement = connection.createStatement()) {
                statement.execute("LISTEN \"" + CHANNEL_NAME + "\"");
            }
            final var unwrapped = connection.unwrap(PGConnection.class);
            while (!Thread.currentThread().isInterrupted() && task.state() == MindustryTask.State.SCHEDULED) {
                final var notifications = unwrapped.getNotifications(1000);
                if (notifications == null) {
                    continue;
                }
                for (final var notification : notifications) {
                    this.executor.execute(() -> this.dispatch(notification));
                }
            }
        } catch (final Exception e) {
            this.logger.error("An error occurred while polling nucleus messages", e);
        }
    }

    // TODO Unslop Later
    @SuppressWarnings("unchecked")
    private <M extends Message> void dispatch(final PGNotification notification) {
        if (!notification.getName().equals(CHANNEL_NAME)) {
            return;
        }
        final var parts = notification.getParameter().split("\\|", 3);
        if (parts.length != 3) {
            return;
        }

        final var payloadType = parts[0];
        final var sender = parts[1];
        final var payload = parts[2];

        final var subscribers = this.subscribers.get(payloadType);
        if (subscribers == null) {
            return;
        }

        final M message;
        try {
            message = this.gson.fromJson(payload, (Class<M>) Class.forName(payloadType));
        } catch (final ClassNotFoundException e) {
            return;
        } catch (final Exception e) {
            this.logger.error("Failed to deserialize message of type {}", payloadType, e);
            return;
        }

        for (final var subscriber : subscribers) {
            try {
                ((MessageSubscriber<M>) subscriber).onMessage(sender, message);
            } catch (final Exception e) {
                this.logger.error("{} failed to handle message {} from {}", subscriber, message, sender, e);
            }
        }
    }

    public record DiscoveredServer(ServerInfo info, Instant seenAt) {}
}
