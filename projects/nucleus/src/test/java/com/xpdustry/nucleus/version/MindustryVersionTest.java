// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MindustryVersionTest {

    @Test
    void parses_a_release_tag() {
        assertThat(new MindustryVersion("v160.7")).isEqualTo(new MindustryVersion(160, 7));
        assertThat(new MindustryVersion("v146")).isEqualTo(new MindustryVersion(146, 0));
        assertThat(new MindustryVersion("160.7")).isEqualTo(new MindustryVersion(160, 7));
    }

    @Test
    void writes_a_release_tag() {
        assertThat(new MindustryVersion(160, 7)).hasToString("v160.7");
        assertThat(new MindustryVersion(146, 0)).hasToString("v146");
    }

    @Test
    void rejects_an_invalid_tag() {
        assertThatThrownBy(() -> new MindustryVersion("v160.7.1")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MindustryVersion("custom")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MindustryVersion("v160.")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void orders_by_build_then_patch() {
        assertThat(new MindustryVersion(160, 7)).isGreaterThan(new MindustryVersion(160, 0));
        assertThat(new MindustryVersion(161, 0)).isGreaterThan(new MindustryVersion(160, 7));
    }

    @Test
    void serializes_as_a_string() {
        final var json = new JsonMapper();
        assertThat(json.writeValueAsString(new MindustryVersion(160, 7))).isEqualTo("\"v160.7\"");
        assertThat(json.readValue("\"v160.7\"", MindustryVersion.class)).isEqualTo(new MindustryVersion(160, 7));
    }
}
