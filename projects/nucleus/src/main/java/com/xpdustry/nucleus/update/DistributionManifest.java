// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import com.xpdustry.nucleus.version.NucleusVersion;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/// The files a server runs for a build of the monorepo, published by its CI as `com.xpdustry:monorepo-distribution`.
record DistributionManifest(
        NucleusVersion version, String commit, Instant committed, int java, Server mindustry, List<Plugin> plugins) {

    static final String ARTIFACT = "monorepo-distribution";

    DistributionManifest {
        plugins = List.copyOf(plugins);
    }

    /// The Mindustry server jar, from the GitHub releases of Mindustry.
    record Server(MindustryVersion version, URI url, String sha256) {}

    /// A plugin jar, published as the classifier "plugin" of the artifact of the same name.
    record Plugin(String name, String sha256, List<String> dependencies) {

        Plugin {
            dependencies = List.copyOf(dependencies);
        }
    }

    static DistributionManifest read(final JsonMapper json, final Path file) throws IOException {
        try {
            return json.readerFor(DistributionManifest.class)
                    .with(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                    .with(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue(file);
        } catch (final JacksonException e) {
            throw new IOException("Invalid distribution manifest " + file, e);
        }
    }

    Optional<Plugin> plugin(final String name) {
        return this.plugins.stream()
                .filter(plugin -> plugin.name().equals(name))
                .findFirst();
    }

    boolean isNewerThan(final DistributionManifest other) {
        if (this.commit.equals(other.commit)) {
            return false;
        }
        final var versions = this.version.compareTo(other.version);
        return versions != 0 ? versions > 0 : this.committed.isAfter(other.committed);
    }

    @Override
    public String toString() {
        return this.version
                + (this.commit.isEmpty()
                        ? ""
                        : " (" + this.commit.substring(0, Math.min(8, this.commit.length())) + ")");
    }
}
