/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.command.argument;

import arc.Core;
import arc.mock.MockSettings;
import mindustry.Vars;
import mindustry.core.NetServer;
import mindustry.gen.Player;
import mindustry.net.Administration.PlayerInfo;
import mindustry.net.Net;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayerInfoArgumentTest
        extends AbstractPlayerLookupArgumentTest<PlayerInfoArgument<Object>, PlayerInfo> {

    private static final String CREATED_UUID = "DAAAAAAAAAAAAAAAAAAAAA==";

    @BeforeEach
    void createAdministration() {
        Core.settings = new MockSettings();
        Vars.net = Mockito.mock(Net.class);
        Vars.netServer = new NetServer();
    }

    @Test
    void test_find_by_created_info() {
        final var argument = this.createArgument();
        final var result = argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue(CREATED_UUID));
        assertThat(result.getParsedValue()).isPresent().get().extracting("id").isEqualTo(CREATED_UUID);
    }

    @Test
    void test_find_by_existing_info() {
        Vars.netServer.admins.updatePlayerJoined(
                this.getPlayer1().uuid(), "0.0.0.0", this.getPlayer1().name());
        final var argument = this.createArgument();
        final var result = argument.getParser()
                .parse(
                        this.getCommandContext(),
                        this.createArgumentQueue(this.getPlayer1().uuid()));
        assertThat(result.getParsedValue())
                .isPresent()
                .get()
                .isEqualTo(this.getPlayer1().getInfo());
    }

    @Override
    protected PlayerInfoArgument<Object> createArgument() {
        return PlayerInfoArgument.of("argument");
    }

    @Override
    protected PlayerInfo mapPlayer(final Player player) {
        return player.getInfo();
    }
}
