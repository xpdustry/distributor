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

import fr.xpdustry.distributor.api.Distributor;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.permission.GroupPermission;
import fr.xpdustry.distributor.api.permission.PlayerPermission;
import fr.xpdustry.distributor.api.secutiry.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class SimplePermissionServiceTest {

    private static final MUUID PLAYER = MUUID.of("AAAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");

    private static final String GROUP1 = "group1";
    private static final String GROUP2 = "group2";

    private static final String PERMISSION1 = "fr.xpdustry.test.a";
    private static final String PERMISSION2 = "fr.xpdustry.test.b";

    @TempDir
    private Path tempDir;

    private SimplePermissionService manager;

    @BeforeAll
    static void setupAll() {
        final var distributor = Mockito.mock(Distributor.class);
        Mockito.when(distributor.getMUUIDAuthenticator()).thenReturn(muuid -> true);
        DistributorProvider.set(distributor);
    }

    @BeforeEach
    void setup() {
        this.manager = new SimplePermissionService(this.tempDir);
        this.manager.setVerifyAdmin(false);
    }

    @Test
    void test_player_save() throws ConfigurateException {
        final var players = this.manager.getPlayerPermissionManager();
        final var player = players.findOrCreateById(PLAYER.getUuid());

        player.setPermission(PERMISSION1, Tristate.TRUE);
        player.setPermission(PERMISSION2, Tristate.FALSE);

        Assertions.assertEquals(0, players.count());
        players.save(player);
        Assertions.assertEquals(1, players.count());

        final var root = YamlConfigurationLoader.builder()
                .path(this.tempDir.resolve("players.yaml"))
                .build()
                .load();
        Assertions.assertTrue(
                root.node(PLAYER.getUuid(), "permissions", PERMISSION1).getBoolean());
        Assertions.assertFalse(
                root.node(PLAYER.getUuid(), "permissions", PERMISSION2).getBoolean());
    }

    @Test
    void test_group_save() throws ConfigurateException {
        final var groups = this.manager.getGroupPermissionManager();
        final var group = groups.findOrCreateById(GROUP1);

        group.setPermission("fr.xpdustry.test.a", Tristate.TRUE);
        group.setPermission("fr.xpdustry.test.b", Tristate.FALSE);
        group.setWeight(6);

        Assertions.assertEquals(0, groups.count());
        groups.save(group);
        Assertions.assertEquals(1, groups.count());

        final var root = YamlConfigurationLoader.builder()
                .path(this.tempDir.resolve("groups.yaml"))
                .build()
                .load();
        Assertions.assertTrue(root.node(GROUP1, "permissions", PERMISSION1).getBoolean());
        Assertions.assertFalse(root.node(GROUP1, "permissions", PERMISSION2).getBoolean());
        Assertions.assertEquals(6, root.node(GROUP1, "weight").getInt());
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

    private void setupPlayer(final Consumer<PlayerPermission> setup) {
        final var players = this.manager.getPlayerPermissionManager();
        final var player = players.findOrCreateById(PLAYER.getUuid());
        setup.accept(player);
        players.save(player);
    }

    private void setupGroup(final String name, final Consumer<GroupPermission> setup) {
        final var groups = this.manager.getGroupPermissionManager();
        final var group = groups.findOrCreateById(name);
        setup.accept(group);
        groups.save(group);
    }
}