// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bouncycastle.openpgp.PGPException;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

/// A Maven repository served over HTTP, publishing the distributions like the monorepo CI, along with the Mindustry
/// servers they list.
final class TestRepository implements AutoCloseable {

    private final Path root;
    private final HttpServer server;
    private final Map<String, List<String>> versions = new HashMap<>();

    TestRepository(final Path root) throws IOException {
        this.root = Files.createDirectories(root);
        this.server = SimpleFileServer.createFileServer(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                root.toAbsolutePath(),
                SimpleFileServer.OutputLevel.NONE);
        this.server.start();
    }

    URI uri() {
        return URI.create("http://127.0.0.1:" + this.server.getAddress().getPort());
    }

    /// Publishes the plugin jars of the distribution, then its manifest signed with the given key.
    ///
    /// @param timestamp the time a snapshot is published at, ignored for a release
    void publish(final TestDistribution distribution, final String timestamp, final String key)
            throws IOException, PGPException {
        for (final var plugin : distribution.plugins().keySet()) {
            final var jar = this.file(distribution, plugin, timestamp, "plugin", "jar");
            Files.write(jar, distribution.jar(plugin));
            TestKeys.sign(jar, key);
        }
        final var server = this.root
                .resolve("github")
                .resolve(distribution.mindustry().toString())
                .resolve("server-release.jar");
        Files.createDirectories(server.getParent());
        Files.write(server, TestDistribution.server(distribution.mindustry()));

        final var manifest = this.file(distribution, DistributionManifest.ARTIFACT, timestamp, null, "json");
        Files.writeString(manifest, new JsonMapper().writeValueAsString(distribution.manifest(this.uri())));
        TestKeys.sign(manifest, key);
    }

    Path jar(final TestDistribution distribution, final String plugin, final String timestamp) {
        return this.directory(distribution, plugin)
                .resolve(plugin + "-" + fileVersion(distribution, timestamp) + "-plugin.jar");
    }

    @Override
    public void close() {
        this.server.stop(0);
    }

    private Path file(
            final TestDistribution distribution,
            final String artifact,
            final String timestamp,
            final @Nullable String classifier,
            final String extension)
            throws IOException {
        final var directory = Files.createDirectories(this.directory(distribution, artifact));
        final var file = fileVersion(distribution, timestamp);
        final var repository = isSnapshot(distribution) ? "snapshots" : "releases";
        this.writeVersions(repository, artifact, distribution.version());
        if (isSnapshot(distribution)) {
            Files.writeString(directory.resolve("maven-metadata.xml"), """
                    <metadata modelVersion="1.1.0">
                      <versioning>
                        <snapshotVersions>
                          <snapshotVersion><extension>pom</extension><value>%1$s</value></snapshotVersion>
                          <snapshotVersion>%2$s<extension>%3$s</extension><value>%1$s</value></snapshotVersion>
                        </snapshotVersions>
                      </versioning>
                    </metadata>
                    """.formatted(
                            file, classifier == null ? "" : "<classifier>" + classifier + "</classifier>", extension));
        }
        return directory.resolve(
                artifact + "-" + file + (classifier == null ? "" : "-" + classifier) + "." + extension);
    }

    private Path directory(final TestDistribution distribution, final String artifact) {
        return this.artifact(isSnapshot(distribution) ? "snapshots" : "releases", artifact)
                .resolve(distribution.version());
    }

    private void writeVersions(final String repository, final String artifact, final String version)
            throws IOException {
        final var versions = this.versions.computeIfAbsent(repository + "/" + artifact, _ -> new ArrayList<>());
        if (!versions.contains(version)) {
            versions.add(version);
        }
        Files.writeString(this.artifact(repository, artifact).resolve("maven-metadata.xml"), """
                <metadata>
                  <groupId>com.xpdustry</groupId>
                  <artifactId>%s</artifactId>
                  <versioning><versions>%s</versions></versioning>
                </metadata>
                """.formatted(
                artifact,
                versions.stream().map(it -> "<version>" + it + "</version>").collect(Collectors.joining())));
    }

    private Path artifact(final String repository, final String artifact) {
        return this.root.resolve(repository).resolve("com").resolve("xpdustry").resolve(artifact);
    }

    private static boolean isSnapshot(final TestDistribution distribution) {
        return distribution.version().endsWith("-SNAPSHOT");
    }

    private static String fileVersion(final TestDistribution distribution, final String timestamp) {
        return isSnapshot(distribution)
                ? distribution.version().replace("-SNAPSHOT", "-" + timestamp)
                : distribution.version();
    }
}
