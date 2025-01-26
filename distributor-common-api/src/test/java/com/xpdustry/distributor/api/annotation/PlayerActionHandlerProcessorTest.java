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
package com.xpdustry.distributor.api.annotation;

import arc.Core;
import arc.mock.MockSettings;
import com.xpdustry.distributor.api.test.TestPlugin;
import mindustry.Vars;
import mindustry.core.NetServer;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Net;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class PlayerActionHandlerProcessorTest {

    private static final Player PLAYER = Mockito.mock(Player.class);
    private PlayerActionHandlerProcessor processor;

    @BeforeEach
    void setup() {
        this.processor = new PlayerActionHandlerProcessor(new TestPlugin("test"));
        Core.settings = new MockSettings();
        Vars.net = Mockito.mock(Net.class);
        Vars.netServer = new NetServer();
    }

    @AfterEach
    void clear() {
        Core.settings = null;
        Vars.net = null;
        Vars.netServer = null;
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        this.processor.process(instance);
        final var allowed =
                Vars.netServer.admins.allowAction(PLAYER, Administration.ActionType.placeBlock, action -> {});
        assertThat(allowed).isTrue();
        assertThat(instance.passed).isTrue();
    }

    @Test
    void test_invalid_parameter() {
        assertThatThrownBy(() -> this.processor.process(new TestInvalidParameter()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_too_many_parameters() {
        assertThatThrownBy(() -> this.processor.process(new TestTooManyParameters()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_invalid_return_type() {
        assertThatThrownBy(() -> this.processor.process(new TestInvalidReturnType()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static final class TestSimple {

        public boolean passed = false;

        @PlayerActionHandler
        public boolean filter(final Administration.PlayerAction action) {
            if (passed) throw new IllegalStateException("Passed is true");
            passed = true;
            return true;
        }
    }

    private static final class TestInvalidParameter {

        @PlayerActionHandler
        public boolean filter(final String what) {
            return true;
        }
    }

    private static final class TestTooManyParameters {

        @PlayerActionHandler
        public boolean filter(final Administration.PlayerAction a1, final Administration.PlayerAction a2) {
            return true;
        }
    }

    private static final class TestInvalidReturnType {

        @PlayerActionHandler
        public String filter(final Administration.PlayerAction action) {
            return "what ?";
        }
    }
}
