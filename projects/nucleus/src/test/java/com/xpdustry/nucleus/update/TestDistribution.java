// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import com.xpdustry.nucleus.version.NucleusVersion;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// A build of the monorepo for the tests, the content of each file telling its plugin and commit apart.
record TestDistribution(
        String version,
        String commit,
        Instant committed,
        int java,
        MindustryVersion mindustry,
        Map<String, List<String>> plugins) {

    static TestDistribution of(final String version, final String commit, final Instant committed) {
        return new TestDistribution(
                version,
                commit.repeat(40),
                committed,
                25,
                new MindustryVersion(160, 7),
                Map.of("foundation", List.of(), "nucleus", List.of("foundation")));
    }

    TestDistribution withJava(final int java) {
        return new TestDistribution(this.version, this.commit, this.committed, java, this.mindustry, this.plugins);
    }

    TestDistribution withMindustry(final MindustryVersion mindustry) {
        return new TestDistribution(this.version, this.commit, this.committed, this.java, mindustry, this.plugins);
    }

    TestDistribution withPlugin(final String name, final String... dependencies) {
        final var plugins = new LinkedHashMap<>(this.plugins);
        plugins.put(name, List.of(dependencies));
        return new TestDistribution(this.version, this.commit, this.committed, this.java, this.mindustry, plugins);
    }

    byte[] jar(final String plugin) {
        return (plugin + " " + this.commit).getBytes(StandardCharsets.UTF_8);
    }

    static byte[] server(final MindustryVersion mindustry) {
        return ("server " + mindustry).getBytes(StandardCharsets.UTF_8);
    }

    /// The manifest, with the Mindustry server served by the given repository.
    DistributionManifest manifest(final URI repository) {
        final var plugins = new ArrayList<DistributionManifest.Plugin>();
        for (final var plugin : this.plugins.entrySet()) {
            plugins.add(new DistributionManifest.Plugin(
                    plugin.getKey(), sha256(this.jar(plugin.getKey())), plugin.getValue()));
        }
        return new DistributionManifest(
                new NucleusVersion(this.version),
                this.commit,
                this.committed,
                this.java,
                new DistributionManifest.Server(
                        this.mindustry,
                        URI.create(repository + "/github/" + this.mindustry + "/server-release.jar"),
                        sha256(server(this.mindustry))),
                plugins);
    }

    static String sha256(final byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
