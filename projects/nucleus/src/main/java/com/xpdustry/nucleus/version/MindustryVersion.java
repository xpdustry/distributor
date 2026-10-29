// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import java.util.Locale;
import java.util.Objects;
import mindustry.core.Version;

public record MindustryVersion(int major, int build, int patch, Type type) {

    public MindustryVersion {
        if (major < 0) {
            throw new IllegalArgumentException("Major version must be positive");
        }
        if (build < 0) {
            throw new IllegalArgumentException("Build version must be positive");
        }
        if (patch < 0) {
            throw new IllegalArgumentException("Patch version must be positive");
        }
        Objects.requireNonNull(type, "type");
    }

    /// Returns the version of the running game.
    public static MindustryVersion current() {
        final Type type;
        if (Version.build == -1) {
            type = Type.CUSTOM;
        } else if ("alpha".equalsIgnoreCase(Version.modifier)) {
            type = Type.ALPHA;
        } else if ("official".equals(Version.type)) {
            type = Type.OFFICIAL;
        } else if ("bleeding-edge".equals(Version.type)) {
            type = Type.BLEEDING_EDGE;
        } else {
            type = Type.CUSTOM;
        }
        return new MindustryVersion(Version.number, Math.max(Version.build, 0), Version.revision, type);
    }

    @Override
    public String toString() {
        final var name = this.type.name().toLowerCase(Locale.ROOT).replace('_', '-');
        return name + " v" + this.major + " " + this.build + (this.patch == 0 ? "" : "." + this.patch);
    }

    public enum Type {
        OFFICIAL,
        ALPHA,
        BLEEDING_EDGE,
        CUSTOM,
    }
}
