// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Comparator;
import mindustry.core.Version;

/// A version of Mindustry, written as its release tag such as `v160.7`.
public record MindustryVersion(int build, int patch) implements Comparable<MindustryVersion> {

    private static final Comparator<MindustryVersion> COMPARATOR =
            Comparator.comparingInt(MindustryVersion::build).thenComparingInt(MindustryVersion::patch);

    public MindustryVersion {
        if (build < 0) {
            throw new IllegalArgumentException("Build must be positive");
        }
        if (patch < 0) {
            throw new IllegalArgumentException("Patch must be positive");
        }
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public MindustryVersion(final String version) {
        final var parts = (version.startsWith("v") ? version.substring(1) : version).split("\\.", -1);
        if (parts.length > 2) {
            throw new IllegalArgumentException("Version must be in format 'v<build>[.<patch>]', got " + version);
        }
        final int build, patch;
        try {
            build = Integer.parseInt(parts[0]);
            patch = parts.length == 2 ? Integer.parseInt(parts[1]) : 0;
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Version must be in format 'v<build>[.<patch>]', got " + version, e);
        }
        this(build, patch);
    }

    /// Returns the version of the running game, a custom build being the build 0.
    public static MindustryVersion current() {
        return new MindustryVersion(Math.max(Version.build, 0), Math.max(Version.revision, 0));
    }

    @Override
    public int compareTo(final MindustryVersion other) {
        return COMPARATOR.compare(this, other);
    }

    @JsonValue
    @Override
    public String toString() {
        return "v" + this.build + (this.patch == 0 ? "" : "." + this.patch);
    }
}
