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
package com.xpdustry.distributor.api.player;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class MUUIDTest {

    private static final String UUID = "Tbp/H+yx8T8AAAAAqI8wmg==";
    private static final String USID = "AAAAAAAAAAA=";

    @Test
    void test_long_conversion() {
        final var muuid1 = MUUID.of(UUID, USID);
        final var muuid2 = MUUID.of(muuid1.getUuidAsLong(), muuid1.getUsidAsLong());
        assertThat(muuid2).isEqualTo(muuid1);
    }

    @Test
    void test_invalid_padding() {
        final var invalidUuid = UUID.replace("=", "");
        final var invalidUsid = USID.replace("=", "");
        assertThatNoException().isThrownBy(() -> MUUID.of(UUID, USID));
        assertThatThrownBy(() -> MUUID.of(UUID, invalidUsid)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MUUID.of(invalidUuid, USID)).isInstanceOf(IllegalArgumentException.class);
    }
}
