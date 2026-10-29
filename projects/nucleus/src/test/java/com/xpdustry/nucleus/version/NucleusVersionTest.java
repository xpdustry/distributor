// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class NucleusVersionTest {

    @Test
    void parses_releases_and_snapshots() {
        assertThat(new NucleusVersion("v2026.10.2")).isEqualTo(new NucleusVersion(2026, 10, 2, false));
        assertThat(new NucleusVersion("2026.10.2-SNAPSHOT")).isEqualTo(new NucleusVersion(2026, 10, 2, true));
    }

    @Test
    void rejects_invalid_versions() {
        assertThatThrownBy(() -> new NucleusVersion("2026.10")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NucleusVersion("2026.13.0")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void orders_snapshots_before_their_release() {
        assertThat(new NucleusVersion("2026.9.4")).isLessThan(new NucleusVersion("2026.10.0-SNAPSHOT"));
        assertThat(new NucleusVersion("2026.10.0-SNAPSHOT")).isLessThan(new NucleusVersion("2026.10.0"));
    }

    @Test
    void prints_like_it_parses() {
        assertThat(new NucleusVersion("2026.10.2-SNAPSHOT")).hasToString("2026.10.2-SNAPSHOT");
        assertThat(new NucleusVersion("v2026.10.2")).hasToString("2026.10.2");
    }
}
