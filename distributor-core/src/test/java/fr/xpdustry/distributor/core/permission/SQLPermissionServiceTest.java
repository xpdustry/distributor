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
package fr.xpdustry.distributor.core.permission;

import fr.xpdustry.distributor.api.permission.GroupPermissible;
import fr.xpdustry.distributor.api.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.DistributorConfiguration;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

public final class SQLPermissionServiceTest {

    private static final MUUID PLAYER = MUUID.of("AAAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");

    private static final String GROUP1 = "group1";
    private static final String GROUP2 = "group2";

    private static final String PERMISSION1 = "fr.xpdustry.test.a";
    private static final String PERMISSION2 = "fr.xpdustry.test.b";

    private SQLiteConnectionFactory factory;
    private @TempDir Path dbDir;
    private SQLPermissionService manager;

    @BeforeEach
    void setup() {
        final var config = Mockito.mock(DistributorConfiguration.class);
        Mockito.when(config.getDatabasePrefix()).thenReturn("test_");
        this.factory = new SQLiteConnectionFactory(
                config, this.dbDir.resolve("test.db"), () -> this.getClass().getClassLoader());
        this.factory.start();
        this.manager = new SQLPermissionService(this.factory);
        this.manager.onPluginLoad();
        this.manager.setVerifyAdmin(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.factory.close();
    }

    @Test
    void test_player_save() {
        final var players = this.manager.getPlayerPermissionManager();
        final var player = players.findOrCreateById(PLAYER.getUuid());

        player.setPermission(PERMISSION1, Tristate.TRUE);
        player.setPermission(PERMISSION2, Tristate.FALSE);

        Assertions.assertEquals(0, players.count());
        players.save(player);
        Assertions.assertEquals(1, players.count());

        final var queried = players.findById(PLAYER.getUuid()).orElseThrow();
        Assertions.assertEquals(queried, player);
        Assertions.assertEquals(Tristate.TRUE, queried.getPermission(PERMISSION1));
        Assertions.assertEquals(Tristate.FALSE, queried.getPermission(PERMISSION2));
    }

    @Test
    void test_group_save() {
        final var groups = this.manager.getGroupPermissionManager();
        final var group = groups.findOrCreateById(GROUP1);

        group.setPermission("fr.xpdustry.test.a", Tristate.TRUE);
        group.setPermission("fr.xpdustry.test.b", Tristate.FALSE);
        group.setWeight(6);

        Assertions.assertEquals(0, groups.count());
        groups.save(group);
        Assertions.assertEquals(1, groups.count());

        final var queried = groups.findById(GROUP1).orElseThrow();
        Assertions.assertEquals(queried, group);
        Assertions.assertEquals(Tristate.TRUE, queried.getPermission(PERMISSION1));
        Assertions.assertEquals(Tristate.FALSE, queried.getPermission(PERMISSION2));
    }

    @Test
    void test_permission_calculation_player() {
        this.setupPlayer(player -> player.setPermission(PERMISSION1, Tristate.TRUE));
        Assertions.assertTrue(this.manager.getPermission(PLAYER, PERMISSION1).asBoolean());
    }

    @Test
    void test_permission_calculation_group() {
        this.setupPlayer(player -> player.addParent(GROUP1));
        this.setupGroup(GROUP1, group -> group.setPermission(PERMISSION1, Tristate.FALSE));
        Assertions.assertFalse(this.manager.getPermission(PLAYER, PERMISSION1).asBoolean());
    }

    @Test
    void test_permission_calculation_group_with_weight() {
        this.setupPlayer(player -> {
            player.addParent(GROUP1);
            player.addParent(GROUP2);
        });

        this.setupGroup(GROUP1, group -> {
            group.setPermission(PERMISSION1, Tristate.FALSE);
            group.setWeight(10);
        });

        this.setupGroup(GROUP2, group -> {
            group.setPermission(PERMISSION1, Tristate.TRUE);
            group.setWeight(20);
        });

        Assertions.assertTrue(this.manager.getPermission(PLAYER, PERMISSION1).asBoolean());
    }

    @Test
    void test_default_group() {
        this.setupGroup(this.manager.getPrimaryGroup(), group -> group.setPermission(PERMISSION2, Tristate.TRUE));
        Assertions.assertTrue(this.manager.getPermission(PLAYER, PERMISSION2).asBoolean());
    }

    private void setupPlayer(final Consumer<PlayerPermissible> setup) {
        final var players = this.manager.getPlayerPermissionManager();
        final var player = players.findOrCreateById(PLAYER.getUuid());
        setup.accept(player);
        players.save(player);
    }

    private void setupGroup(final String name, final Consumer<GroupPermissible> setup) {
        final var groups = this.manager.getGroupPermissionManager();
        final var group = groups.findOrCreateById(name);
        setup.accept(group);
        groups.save(group);
    }
}
