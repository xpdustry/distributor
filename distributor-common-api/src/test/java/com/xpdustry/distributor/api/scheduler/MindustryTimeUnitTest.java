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
package com.xpdustry.distributor.api.scheduler;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public final class MindustryTimeUnitTest {

    @ParameterizedTest
    @EnumSource(MindustryTimeUnit.class)
    void test_java_unit(final MindustryTimeUnit unit) {
        if (unit != MindustryTimeUnit.TICKS) {
            assertThat(unit.getJavaTimeUnit()).isPresent();
        } else {
            assertThat(unit.getJavaTimeUnit()).isEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("provideJavaMindustryTimeUnits")
    void test_java_conversion(final MindustryTimeUnit unitA, final MindustryTimeUnit unitB) {
        assertThat(unitA.getJavaTimeUnit()
                        .orElseThrow()
                        .convert(1000L, unitB.getJavaTimeUnit().orElseThrow()))
                .isEqualTo(unitA.convert(1000L, unitB));
    }

    @Test
    void test_tick_conversion() {
        assertThat(MindustryTimeUnit.TICKS.convert(60L, MindustryTimeUnit.TICKS))
                .isEqualTo(60L);

        assertThat(MindustryTimeUnit.TICKS.convert(2L, MindustryTimeUnit.SECONDS))
                .isEqualTo(120L);

        assertThat(MindustryTimeUnit.SECONDS.convert(120L, MindustryTimeUnit.TICKS))
                .isEqualTo(2L);
    }

    @Test
    void test_tick_conversion_overflow() {
        assertThat(MindustryTimeUnit.TICKS.convert(+10_000_000_000_000L, MindustryTimeUnit.DAYS))
                .isEqualTo(Long.MAX_VALUE);

        assertThat(MindustryTimeUnit.TICKS.convert(-10_000_000_000_000L, MindustryTimeUnit.DAYS))
                .isEqualTo(Long.MIN_VALUE);
    }

    private static Stream<Arguments> provideJavaMindustryTimeUnits() {
        return Arrays.stream(MindustryTimeUnit.values())
                .filter(unit -> unit != MindustryTimeUnit.TICKS)
                .flatMap(unit1 -> Arrays.stream(MindustryTimeUnit.values())
                        .filter(unit2 -> unit2 != MindustryTimeUnit.TICKS)
                        .map(unit2 -> Arguments.of(unit1, unit2)));
    }
}
