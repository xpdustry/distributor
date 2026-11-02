// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class UpdateStagerTest {

    private static final Instant MONDAY = Instant.parse("2026-10-26T12:00:00Z");
    private static final Instant TUESDAY = Instant.parse("2026-10-27T12:00:00Z");
    private static final String TIMESTAMP = "20261027.120000-2";
    private static final TestDistribution CURRENT = TestDistribution.of("2026.10.0", "a", MONDAY);
    private static final TestDistribution NEXT = TestDistribution.of("2026.10.1", "b", TUESDAY);

    @TempDir
    private Path directory;

    private final HttpClient http = HttpClient.newHttpClient();
    private TestRepository repository;
    private UpdateStager stager;
    private DistributionInfo installed;

    @BeforeEach
    void setup() throws IOException {
        this.repository = new TestRepository(this.directory.resolve("repository"));
        this.stager = new UpdateStager(new JsonMapper(), 25);
        this.installed = this.install(CURRENT);
    }

    @AfterEach
    void teardown() {
        this.repository.close();
        this.http.close();
    }

    @Test
    void stages_a_newer_release() throws Exception {
        this.publish(NEXT);
        final var update = this.stage(UpdateChannel.RELEASE, CURRENT);
        assertThat(update).isPresent();
        assertThat(update.get().manifest().version()).hasToString("2026.10.1");
        assertThat(update.get().requiresRestart()).isTrue();
        assertThat(update.get().files())
                .containsOnlyKeys(this.installed.plugins().values());
        for (final var plugin : this.installed.plugins().entrySet()) {
            assertThat(update.get().files().get(plugin.getValue())).hasBinaryContent(NEXT.jar(plugin.getKey()));
        }
    }

    @Test
    void stages_a_newer_snapshot_of_the_same_version() throws Exception {
        final var current = TestDistribution.of("2026.10.1-SNAPSHOT", "a", MONDAY);
        this.installed = this.install(current);
        this.publish(TestDistribution.of("2026.10.1-SNAPSHOT", "b", TUESDAY));
        assertThat(this.stage(UpdateChannel.SNAPSHOT, current))
                .hasValueSatisfying(it -> assertThat(it.manifest().commit()).isEqualTo("b".repeat(40)));
    }

    @Test
    void skips_the_current_distribution() throws Exception {
        this.publish(CURRENT);
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT)).isEmpty();
    }

    @Test
    void never_downgrades() throws Exception {
        this.publish(CURRENT);
        assertThat(this.stage(UpdateChannel.RELEASE, NEXT)).isEmpty();
    }

    @Test
    void stages_only_the_changed_files() throws Exception {
        Files.write(this.installed.plugins().get("foundation"), NEXT.jar("foundation"));
        this.publish(NEXT);
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT))
                .hasValueSatisfying(it -> assertThat(it.files())
                        .containsOnlyKeys(this.installed.plugins().get("nucleus")));
    }

    @Test
    void installs_the_missing_dependencies() throws Exception {
        final var next = NEXT.withPlugin("sql-postgresql").withPlugin("nucleus", "foundation", "sql-postgresql");
        this.publish(next);
        final var added = this.installed.mods().resolve("sql-postgresql.jar");
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT))
                .hasValueSatisfying(
                        it -> assertThat(it.files().get(added)).hasBinaryContent(next.jar("sql-postgresql")));
    }

    @Test
    void leaves_alone_the_plugins_not_installed() throws Exception {
        this.publish(NEXT.withPlugin("sql-postgresql"));
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT))
                .hasValueSatisfying(it -> assertThat(it.files())
                        .doesNotContainKey(this.installed.mods().resolve("sql-postgresql.jar")));
    }

    @Test
    void stages_the_server_of_a_newer_mindustry() throws Exception {
        this.publish(NEXT.withMindustry(new MindustryVersion(161, 0)));
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT))
                .hasValueSatisfying(it -> assertThat(it.files().get(this.installed.server()))
                        .hasBinaryContent(TestDistribution.server(new MindustryVersion(161, 0))));
    }

    @Test
    void adopts_the_installed_distribution_when_unknown() throws Exception {
        this.publish(CURRENT);
        assertThat(this.stage(UpdateChannel.RELEASE, null))
                .hasValueSatisfying(it -> assertThat(it.requiresRestart()).isFalse());
    }

    @Test
    void refuses_a_distribution_signed_by_another_subkey() throws Exception {
        this.repository.publish(NEXT, TIMESTAMP, TestKeys.OTHER_SIGNING_KEY);
        assertThatThrownBy(() -> this.stage(UpdateChannel.RELEASE, CURRENT))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("is not signed by xpdustry");
    }

    @Test
    void retries_the_files_not_matching_the_distribution_yet() throws Exception {
        this.publish(NEXT);
        final var jar = this.repository.jar(NEXT, "nucleus", TIMESTAMP);
        Files.writeString(jar, "a nucleus from another build");
        TestKeys.sign(jar, TestKeys.TRUSTED_SIGNING_KEY);
        assertThatThrownBy(() -> this.stage(UpdateChannel.RELEASE, CURRENT))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("does not match the distribution");

        Files.write(jar, NEXT.jar("nucleus"));
        TestKeys.sign(jar, TestKeys.TRUSTED_SIGNING_KEY);
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT)).isPresent();
    }

    @Test
    void reports_a_newer_java_once() throws Exception {
        this.publish(NEXT.withJava(26));
        assertThatThrownBy(() -> this.stage(UpdateChannel.RELEASE, CURRENT))
                .isInstanceOf(IncompatibleUpdateException.class)
                .hasMessageContaining("needs Java 26");
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT)).isEmpty();
    }

    @Test
    void skips_the_distribution_it_already_checked() throws Exception {
        this.publish(NEXT);
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT)).isPresent();
        assertThat(this.stage(UpdateChannel.RELEASE, CURRENT)).isEmpty();
    }

    @Test
    void tells_whether_a_distribution_is_installed() throws Exception {
        final var uri = this.repository.uri();
        assertThat(UpdateStager.isInstalled(CURRENT.manifest(uri), this.installed))
                .isTrue();
        assertThat(UpdateStager.isInstalled(NEXT.manifest(uri), this.installed)).isFalse();
        assertThat(UpdateStager.isInstalled(
                        CURRENT.withMindustry(new MindustryVersion(161, 0)).manifest(uri), this.installed))
                .isFalse();
    }

    private void publish(final TestDistribution distribution) throws Exception {
        this.repository.publish(distribution, TIMESTAMP, TestKeys.TRUSTED_SIGNING_KEY);
    }

    private Optional<UpdateStager.StagedUpdate> stage(
            final UpdateChannel channel, final @Nullable TestDistribution current) throws Exception {
        final var staging = this.directory.resolve("staging");
        UpdateStager.clear(staging);
        return this.stager.stage(
                new ArtifactResolver(
                        this.http, this.repository.uri(), TestKeys.verifier(TestKeys.TRUSTED_SIGNER), staging),
                channel,
                current == null ? null : current.manifest(this.repository.uri()),
                this.installed);
    }

    /// Installs the foundation and nucleus of the distribution, on its Mindustry server.
    private DistributionInfo install(final TestDistribution distribution) throws IOException {
        final var server = this.directory.resolve("server.jar");
        Files.write(server, TestDistribution.server(distribution.mindustry()));
        final var mods =
                Files.createDirectories(this.directory.resolve("config").resolve("mods"));
        final var foundation = Files.write(mods.resolve("foundation.jar"), distribution.jar("foundation"));
        final var nucleus = Files.write(mods.resolve("nucleus.jar"), distribution.jar("nucleus"));
        return new DistributionInfo(
                server, distribution.mindustry(), mods, Map.of("foundation", foundation, "nucleus", nucleus));
    }
}
