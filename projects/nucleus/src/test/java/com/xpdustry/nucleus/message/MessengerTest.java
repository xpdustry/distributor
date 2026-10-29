// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import com.xpdustry.foundation.scheduler.MindustryTask;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.database.DatabaseModule;
import com.xpdustry.nucleus.database.PostgresDatabase;
import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.testing.TestApplication;
import com.xpdustry.nucleus.version.MindustryVersion;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

// TODO Unslop Later
final class MessengerTest {

    private static final ServerInfo SERVER_INFO = new ServerInfo(
            "127.0.0.1",
            6567,
            "Ancient Caldera",
            "A test server",
            12,
            3,
            30,
            new MindustryVersion(8, 157, 0, MindustryVersion.Type.OFFICIAL),
            ServerInfo.Gamemode.SURVIVAL,
            null,
            ServerInfo.State.PLAYING);

    private static final PollingTask POLLING = new PollingTask();

    @TempDir
    private static Path directory;

    private static final List<TestApplication> applications = new ArrayList<>();
    private static final List<Thread> pollers = new ArrayList<>();
    private static PostgresDatabase database;
    private static Messenger sender;
    private static Messenger receiver;

    @BeforeAll
    static void setup() throws InterruptedException {
        database = start(new DatabaseModule()).resolve(PostgresDatabase.class);
        sender = messenger("sender");
        receiver = messenger("receiver");
        await(() -> countListeners() == 2);
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        POLLING.cancel();
        for (final var poller : pollers) {
            poller.interrupt();
            poller.join();
        }
        applications.reversed().forEach(TestApplication::close);
    }

    @Test
    void delivers_messages_to_every_server_the_sender_included() throws InterruptedException {
        final var deliveries = new ConcurrentLinkedQueue<Delivery>();
        sender.subscribe(TestMessage.class, (from, message) -> deliveries.add(new Delivery("sender", from, message)));
        receiver.subscribe(
                TestMessage.class, (from, message) -> deliveries.add(new Delivery("receiver", from, message)));

        sender.publish(new TestMessage("Hello world"));

        await(() -> deliveries.size() == 2);
        assertThat(deliveries)
                .containsExactlyInAnyOrder(
                        new Delivery("sender", "sender", new TestMessage("Hello world")),
                        new Delivery("receiver", "sender", new TestMessage("Hello world")));
    }

    @Test
    void discovers_servers_until_they_leave() throws InterruptedException {
        sender.onDiscoveryHeartbeat();
        await(() -> receiver.servers().containsKey("sender"));
        assertThat(receiver.servers().get("sender"))
                .extracting(Messenger.DiscoveredServer::info)
                .isEqualTo(SERVER_INFO);

        sender.publish(new DiscoveryMessage(null));
        await(() -> !receiver.servers().containsKey("sender"));
    }

    private static TestApplication start(final DependencyModule... modules) {
        final var application = TestApplication.start(directory, modules);
        applications.add(application);
        return application;
    }

    private static Messenger messenger(final String name) {
        final var messenger = start(
                        binder -> {
                            binder.bind(PostgresDatabase.class, database);
                            binder.bind(
                                    ConfigManager.class, new ConfigManager().set(ConfigPropertyKey.SERVER_NAME, name));
                        },
                        new MessageModule(),
                        binder -> binder.bind(ServerInfoProvider.class, () -> SERVER_INFO))
                .resolve(Messenger.class);
        pollers.add(Thread.ofVirtual().start(() -> messenger.onNotificationPoll(POLLING)));
        return messenger;
    }

    private static int countListeners() {
        return database.withTransaction(transaction -> transaction
                .prepareStatement("SELECT count(*) FROM pg_stat_activity WHERE query = ?")
                .bind("LISTEN \"nucleus_message_v1\"")
                .executeSelect(result -> result.getInt(1))
                .getFirst());
    }

    private static void await(final BooleanSupplier condition) throws InterruptedException {
        final var deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (!condition.getAsBoolean()) {
            if (System.nanoTime() > deadline) {
                throw new AssertionError("Timed out waiting for the condition");
            }
            Thread.sleep(10L);
        }
    }

    private static final class PollingTask implements MindustryTask {

        private volatile boolean polling = true;

        @Override
        public State state() {
            return this.polling ? State.SCHEDULED : State.CANCELLED;
        }

        @Override
        public void cancel() {
            this.polling = false;
        }
    }

    private record Delivery(String recipient, String sender, TestMessage message) {}

    private record TestMessage(String content) implements Message {}
}
