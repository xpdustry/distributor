// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import arc.Core;
import arc.mock.MockSettings;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryThread;
import mindustry.Vars;
import mindustry.core.NetServer;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Net;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class PlayerActionHandlerProcessorTest {

    private static final Player PLAYER = mock(Player.class);

    private PlayerActionHandlerProcessor processor;

    @BeforeEach
    void setup() {
        MindustryThread.captureMainThread();
        Core.settings = new MockSettings();
        Vars.net = mock(Net.class);
        Vars.netServer = new NetServer();
        this.processor = new PlayerActionHandlerProcessor(mock(PluginFacade.class), Vars.netServer.admins);
    }

    @AfterEach
    void clear() {
        Core.settings = null;
        Vars.net = null;
        Vars.netServer = null;
        MindustryThread.clear();
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        final var initialFilterCount = Vars.netServer.admins.actionFilters.size;
        final var subscription = this.processor.process(instance).orElseThrow();

        final var allowed = Vars.netServer.admins.allowAction(PLAYER, Administration.ActionType.placeBlock, _ -> {});

        assertThat(allowed).isTrue();
        assertThat(instance.passed).isTrue();

        subscription.unsubscribe();
        assertThat(Vars.netServer.admins.actionFilters).hasSize(initialFilterCount);
    }

    @Test
    void test_invalid_parameter() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestInvalidParameter()));
    }

    @Test
    void test_too_many_parameters() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestTooManyParameters()));
    }

    @Test
    void test_invalid_return_type() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestInvalidReturnType()));
    }

    private static final class TestSimple {
        private boolean passed = false;

        @PlayerActionHandler
        private boolean filter(final Administration.PlayerAction action) {
            this.passed = true;
            return true;
        }
    }

    private static final class TestInvalidParameter {
        @PlayerActionHandler
        private boolean filter(final String value) {
            return true;
        }
    }

    private static final class TestTooManyParameters {
        @PlayerActionHandler
        private boolean filter(final Administration.PlayerAction first, final Administration.PlayerAction second) {
            return true;
        }
    }

    private static final class TestInvalidReturnType {
        @PlayerActionHandler
        private String filter(final Administration.PlayerAction action) {
            return "invalid";
        }
    }
}
