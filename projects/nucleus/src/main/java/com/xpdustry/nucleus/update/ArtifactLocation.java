// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.NucleusVersion;
import java.net.URI;
import org.jspecify.annotations.Nullable;

sealed interface ArtifactLocation {

    /// A file of a `com.xpdustry` artifact, signed by xpdustry.
    record Maven(String artifact, Version version, @Nullable String classifier, String extension)
            implements ArtifactLocation {

        @Override
        public String toString() {
            final var version = switch (this.version) {
                case Version.Latest latest -> latest.snapshot() ? "latest snapshot" : "latest release";
                case Version.Exact exact -> exact.version().toString();
            };
            return "com.xpdustry:" + this.artifact + ":" + version
                    + (this.classifier == null ? "" : ":" + this.classifier) + "@" + this.extension;
        }
    }

    /// A file outside the xpdustry repository, such as the Mindustry server, verified by whoever trusts its source.
    record Remote(URI uri, String name) implements ArtifactLocation {}

    sealed interface Version {

        record Latest(boolean snapshot) implements Version {}

        record Exact(NucleusVersion version) implements Version {}
    }
}
