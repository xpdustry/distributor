/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.api.permission.rank;

import com.xpdustry.distributor.api.permission.TriState;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public final class YamlRankPermissionSourceTest {

    private static final String TEST_CONFIG_VALID =
            """
            version: 1
            ranks:
              low:
                test1.sub: false
                test2: true
              high:
                test1: true
                test2: false
            """;

    private static final String TEST_CONFIG_VERSION_MISMATCH = """
            version: 2
            """;

    private static final String TEST_CONFIG_INVALID_RANK =
            """
            version: 1
            ranks:
              '':
                test1: true
            """;

    @Test
    void test_simple() throws IOException {
        final var source = createSource(TEST_CONFIG_VALID);
        source.reload();
        final var rank1 = EnumRankNode.linear(TestRank.LOW, true);
        final var rank2 = EnumRankNode.linear(TestRank.HIGH, true);

        final var permissions1 = source.getRankPermissions(rank1);
        final var permissions2 = source.getRankPermissions(rank2);

        assertThat(permissions1.getPermission("test1.sub")).isEqualTo(TriState.FALSE);
        assertThat(permissions1.getPermission("test1")).isEqualTo(TriState.UNDEFINED);
        assertThat(permissions1.getPermission("test2")).isEqualTo(TriState.TRUE);
        assertThat(permissions1.getPermission("unknown")).isEqualTo(TriState.UNDEFINED);

        assertThat(permissions2.getPermission("test1")).isEqualTo(TriState.TRUE);
        assertThat(permissions2.getPermission("test1.sub")).isEqualTo(TriState.TRUE);
        assertThat(permissions2.getPermission("test2")).isEqualTo(TriState.FALSE);
        assertThat(permissions2.getPermission("unknown")).isEqualTo(TriState.UNDEFINED);
    }

    @Test
    void test_circular_detection() throws IOException {
        final var source = createSource(TEST_CONFIG_VALID);
        source.reload();
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            assertThatThrownBy(() -> source.getRankPermissions(CircularRankNode.of(0)))
                    .isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    void test_version_mismatch() {
        final var source = createSource(TEST_CONFIG_VERSION_MISMATCH);
        assertThatThrownBy(source::reload).isInstanceOf(IOException.class);
    }

    @Test
    void test_invalid_rank() {
        final var source = createSource(TEST_CONFIG_INVALID_RANK);
        assertThatThrownBy(source::reload).isInstanceOf(IOException.class);
    }

    private YamlRankPermissionSource createSource(final String content) {
        return new YamlRankPermissionSource(() -> new BufferedReader(new StringReader(content)));
    }
}
