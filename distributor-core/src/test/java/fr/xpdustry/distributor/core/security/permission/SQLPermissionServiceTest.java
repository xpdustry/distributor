/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.core.security.permission;

import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.DistributorConfiguration;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public final class SQLPermissionServiceTest {

    private static final MUUID PLAYER = MUUID.of("AAAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");

    private static final String DEFAULT_GROUP = "default";
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
        Mockito.when(config.getPermissionPrimaryGroup()).thenReturn(DEFAULT_GROUP);
        Mockito.when(config.isAdminIgnored()).thenReturn(true);

        final var validator = Mockito.mock(PlayerValidator.class);
        Mockito.when(validator.isValid(PLAYER)).thenReturn(true);

        this.factory = new SQLiteConnectionFactory(
                "test_", this.dbDir.resolve("test.db"), this.getClass().getClassLoader());
        this.factory.start();

        this.manager = new SQLPermissionService(config, this.factory, validator);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.factory.close();
    }

    @Test
    void test_permission_calculation_player() {
        this.createPlayer(player -> player.setPermission(PERMISSION1, Tristate.TRUE));
        assertThat(this.manager.getPlayerPermission(PLAYER, PERMISSION1).asBoolean())
                .isTrue();
    }

    @Test
    void test_permission_calculation_group() {
        this.createPlayer(player -> player.addParentGroup(GROUP1));
        this.createGroup(GROUP1, group -> group.setPermission(PERMISSION1, Tristate.FALSE));
        assertThat(this.manager.getPlayerPermission(PLAYER, PERMISSION1).asBoolean())
                .isFalse();
    }

    @Test
    void test_permission_calculation_group_with_weight() {
        this.createPlayer(player -> {
            player.addParentGroup(GROUP1);
            player.addParentGroup(GROUP2);
        });

        this.createGroup(GROUP1, group -> {
            group.setPermission(PERMISSION1, Tristate.FALSE);
            group.setWeight(10);
        });

        this.createGroup(GROUP2, group -> {
            group.setPermission(PERMISSION1, Tristate.TRUE);
            group.setWeight(20);
        });

        assertThat(this.manager.getPlayerPermission(PLAYER, PERMISSION1).asBoolean())
                .isTrue();
    }

    @Test
    void test_default_group() {
        this.createGroup(DEFAULT_GROUP, group -> group.setPermission(PERMISSION2, Tristate.TRUE));
        assertThat(this.manager.getPlayerPermission(PLAYER, PERMISSION2).asBoolean())
                .isTrue();
    }

    private void createPlayer(final Consumer<PlayerPermissible> setup) {
        final var players = this.manager.getPlayerPermissionManager();
        final var player = players.findOrCreateById(SQLPermissionServiceTest.PLAYER.getUuid());
        setup.accept(player);
        players.save(player);
    }

    private void createGroup(final String name, final Consumer<GroupPermissible> setup) {
        final var groups = this.manager.getGroupPermissionManager();
        final var group = groups.findOrCreateById(name);
        setup.accept(group);
        groups.save(group);
    }
}
