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
package com.xpdustry.distributor.api.permission;

import com.xpdustry.distributor.api.util.TriState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class MutablePermissionTreeImplTest {

    @Test
    void test_permission_set() {
        final var tree = new MutablePermissionTreeImpl();

        assertThat(tree.getPermission("test1")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test1")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.children).isEmpty();

        tree.setPermission("test1", true);
        tree.setPermission("test1.a", true);
        tree.setPermission("test1.b", false);
        tree.setPermission("test2", false);
        tree.setPermission("test2.a", true);

        assertThat(tree.getPermission("test1")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.b")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test1.c")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test2")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test2.a")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test2.b")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test3")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test3.a")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.children).hasSize(2);
        assertThat(tree.children.get("test1").children).hasSize(2);
        assertThat(tree.children.get("test2").children).hasSize(1);
    }

    @Test
    void test_permission_set_undefined() {
        final var tree = new MutablePermissionTreeImpl();

        tree.setPermission("test1", true);
        tree.setPermission("test1.a", false);
        tree.setPermission("test1.a.b", true);

        assertThat(tree.getPermission("test1")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test1.a.b")).isEqualTo(TriState.TRUE);
        assertThat(tree.children).hasSize(1);
        assertThat(tree.children.get("test1").children).hasSize(1);

        tree.setPermission("test1.a", TriState.UNDEFINED);

        assertThat(tree.getPermission("test1")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.a.b")).isEqualTo(TriState.TRUE);
        assertThat(tree.children).hasSize(1);
        assertThat(tree.children.get("test1").children).hasSize(1);

        tree.setPermission("test1", TriState.UNDEFINED);
        assertThat(tree.getPermission("test1")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test1.a.b")).isEqualTo(TriState.TRUE);
        assertThat(tree.children).hasSize(1);
        assertThat(tree.children.get("test1").children).hasSize(1);

        tree.setPermission("test1", TriState.UNDEFINED, true);
        assertThat(tree.getPermission("test1")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.getPermission("test1.a.b")).isEqualTo(TriState.UNDEFINED);
        assertThat(tree.children).isEmpty();
    }

    @Test
    void test_wildcard() {
        final var tree = new MutablePermissionTreeImpl();

        tree.setPermission("*", true);
        tree.setPermission("test1", false);
        tree.setPermission("test1.*", true);
        tree.setPermission("test1.a.b", false);

        assertThat(tree.getPermission("test2")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test1.a")).isEqualTo(TriState.TRUE);
        assertThat(tree.getPermission("test1.a.b")).isEqualTo(TriState.FALSE);
        assertThat(tree.getPermission("test1.b")).isEqualTo(TriState.TRUE);
        assertThat(tree.children).hasSize(2);
        assertThat(tree.children.get("*").children).isEmpty();
        assertThat(tree.children.get("test1").children).hasSize(2);
    }

    @ParameterizedTest()
    @ValueSource(strings = {"(test)", "*.*", "test.*.a", "test.^", "test.", ".test", "..", "    ", "", "test. "})
    void test_throw_on_invalid(final String permission) {
        final var tree = new MutablePermissionTreeImpl();
        assertThatThrownBy(() -> tree.setPermission(permission, true)).isInstanceOf(IllegalArgumentException.class);
    }
}
