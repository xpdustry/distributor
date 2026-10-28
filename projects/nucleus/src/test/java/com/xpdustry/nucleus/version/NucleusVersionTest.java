// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class NucleusVersionTest {

    @Test
    void parse() {
        assertThat(NucleusVersion.parse("2026.10.2")).isEqualTo(new NucleusVersion(2026, 10, 2));
    }

    @Test
    void parseInvalid() {
        assertThatThrownBy(() -> NucleusVersion.parse("2026.10")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compare() {
        assertThat(NucleusVersion.parse("2026.9.4")).isLessThan(NucleusVersion.parse("2026.10.0"));
    }
}
