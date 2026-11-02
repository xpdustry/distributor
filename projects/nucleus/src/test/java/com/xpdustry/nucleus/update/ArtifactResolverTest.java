// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import com.xpdustry.nucleus.version.NucleusVersion;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ArtifactResolverTest {

    private static final String TIMESTAMP = "20261027.120000-2";
    private static final TestDistribution SNAPSHOT =
            TestDistribution.of("2026.10.1-SNAPSHOT", "b", Instant.parse("2026-10-27T12:00:00Z"));

    @TempDir
    private Path directory;

    private final HttpClient http = HttpClient.newHttpClient();
    private TestRepository repository;
    private ArtifactResolver resolver;

    @BeforeEach
    void setup() throws IOException {
        this.repository = new TestRepository(this.directory.resolve("repository"));
        this.resolver = new ArtifactResolver(
                this.http,
                this.repository.uri(),
                TestKeys.verifier(TestKeys.TRUSTED_SIGNER),
                this.directory.resolve("resolved"));
    }

    @AfterEach
    void teardown() {
        this.repository.close();
        this.http.close();
    }

    @Test
    void resolves_the_timestamped_file_of_a_snapshot() throws Exception {
        this.repository.publish(SNAPSHOT, TIMESTAMP, TestKeys.TRUSTED_SIGNING_KEY);
        final var jar = this.resolver.resolve(this.nucleus());
        assertThat(jar).hasFileName("nucleus-2026.10.1-" + TIMESTAMP + "-plugin.jar");
        assertThat(jar).hasBinaryContent(SNAPSHOT.jar("nucleus"));
        assertThat(jar.resolveSibling(jar.getFileName() + ".asc")).doesNotExist();
    }

    @Test
    void refuses_a_tampered_file() throws Exception {
        this.repository.publish(SNAPSHOT, TIMESTAMP, TestKeys.TRUSTED_SIGNING_KEY);
        Files.writeString(this.repository.jar(SNAPSHOT, "nucleus", TIMESTAMP), "a malicious nucleus");
        assertThatThrownBy(() -> this.resolver.resolve(this.nucleus()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("is not signed by xpdustry");
        assertThat(this.directory.resolve("resolved")).isEmptyDirectory();
    }

    @Test
    void refuses_a_missing_artifact() {
        assertThatThrownBy(() -> this.resolver.resolve(this.nucleus()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("No file published");
    }

    @Test
    void resolves_a_remote_file_as_is() throws Exception {
        this.repository.publish(SNAPSHOT, TIMESTAMP, TestKeys.TRUSTED_SIGNING_KEY);
        final var uri = URI.create(this.repository.uri() + "/github/v160.7/server-release.jar");
        assertThat(this.resolver.resolve(new ArtifactLocation.Remote(uri, "server.jar")))
                .hasFileName("server.jar")
                .hasBinaryContent(TestDistribution.server(new MindustryVersion(160, 7)));
    }

    private ArtifactLocation nucleus() {
        return new ArtifactLocation.Maven(
                "nucleus",
                new ArtifactLocation.Version.Exact(new NucleusVersion("2026.10.1-SNAPSHOT")),
                "plugin",
                "jar");
    }
}
