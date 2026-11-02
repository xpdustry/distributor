// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class DistributionManifestTest {

    private static final URI REPOSITORY = URI.create("http://localhost");
    private static final Instant MONDAY = Instant.parse("2026-10-26T12:00:00Z");
    private static final Instant TUESDAY = Instant.parse("2026-10-27T12:00:00Z");

    @TempDir
    private Path directory;

    @Test
    void reads_the_manifest_of_the_build() throws IOException {
        final var file =
                Files.writeString(this.directory.resolve("manifest.json"), TestKeys.resource("distribution.json"));
        final var manifest = DistributionManifest.read(new JsonMapper(), file);
        assertThat(manifest.version()).hasToString("2026.10.0-SNAPSHOT");
        assertThat(manifest.java()).isEqualTo(25);
        assertThat(manifest.mindustry().version()).isEqualTo(new MindustryVersion(160, 7));
        assertThat(manifest.plugins())
                .extracting(DistributionManifest.Plugin::name)
                .containsExactly("foundation", "nucleus", "sql-postgresql", "sql-postgresql-embedded");
        assertThat(manifest.plugin("nucleus"))
                .hasValueSatisfying(
                        it -> assertThat(it.dependencies()).containsExactly("foundation", "sql-postgresql"));
        assertThat(manifest.plugin("foundation"))
                .hasValueSatisfying(it -> assertThat(it.dependencies()).isEmpty());
    }

    @Test
    void rejects_a_missing_field() throws IOException {
        final var file = Files.writeString(
                this.directory.resolve("manifest.json"),
                TestKeys.resource("distribution.json").replace("\"java\": 25,", ""));
        assertThatThrownBy(() -> DistributionManifest.read(new JsonMapper(), file))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid distribution manifest");
    }

    @Test
    void orders_by_version_then_commit_time() {
        final var release = TestDistribution.of("2026.10.1", "a", TUESDAY).manifest(REPOSITORY);
        final var snapshot =
                TestDistribution.of("2026.10.1-SNAPSHOT", "b", MONDAY).manifest(REPOSITORY);
        final var later =
                TestDistribution.of("2026.10.1-SNAPSHOT", "c", TUESDAY).manifest(REPOSITORY);
        assertThat(release.isNewerThan(snapshot)).isTrue();
        assertThat(snapshot.isNewerThan(release)).isFalse();
        assertThat(later.isNewerThan(snapshot)).isTrue();
        assertThat(snapshot.isNewerThan(later)).isFalse();
    }

    @Test
    void is_not_newer_than_the_same_commit() {
        final var manifest = TestDistribution.of("2026.10.1", "a", TUESDAY).manifest(REPOSITORY);
        final var rebuilt = TestDistribution.of("2026.10.2", "a", TUESDAY).manifest(REPOSITORY);
        assertThat(rebuilt.isNewerThan(manifest)).isFalse();
    }
}
