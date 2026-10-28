// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.plugin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class PluginLoggerTest {

    @Test
    void name() {
        assertThat(new PluginLogger("foundation").name()).isEqualTo("foundation");
    }
}
