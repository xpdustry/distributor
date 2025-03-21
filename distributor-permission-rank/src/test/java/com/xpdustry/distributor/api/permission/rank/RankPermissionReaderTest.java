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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.key.Key;
import com.xpdustry.distributor.api.permission.MutablePermissionTree;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.util.Priority;
import com.xpdustry.distributor.api.util.TriState;
import com.xpdustry.distributor.common.service.ServiceManagerImpl;
import java.util.List;
import mindustry.gen.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public final class RankPermissionReaderTest {

    private RankPlayerPermissionProvider reader;
    private ServiceManager services;
    private @Mock MindustryPlugin plugin;
    private @Mock Player player1;
    private @Mock Player player2;
    private @Mock Player player3;
    private @Mock RankProvider rankProvider;

    @BeforeEach
    void setup() {
        this.reader = new RankPlayerPermissionProvider();
        this.services = new ServiceManagerImpl();
        this.services.register(this.plugin, RankProvider.class, this.rankProvider, Priority.NORMAL);
        final var distributor = Mockito.mock(Distributor.class);
        Mockito.when(distributor.getServiceManager()).thenReturn(this.services);
        Distributor.set(distributor);
    }

    @AfterEach
    void teardown() {
        Distributor.set(null);
    }

    @Test
    void test_simple() {
        final var rank1 = EnumRankNode.linear(TestRank.LOW, Key.DISTRIBUTOR_NAMESPACE, true);
        final var rank2 = EnumRankNode.linear(TestRank.HIGH, Key.DISTRIBUTOR_NAMESPACE, true);
        final var permissions1 = MutablePermissionTree.create();
        permissions1.setPermission("test", false);
        final var permissions2 = MutablePermissionTree.create();
        permissions2.setPermission("test", true);
        Mockito.when(this.rankProvider.getRanks(this.player1)).thenReturn(List.of(rank1));
        Mockito.when(this.rankProvider.getRanks(this.player2)).thenReturn(List.of(rank2));
        Mockito.when(this.rankProvider.getRanks(this.player3)).thenReturn(List.of());

        final var permissionSource = Mockito.mock(RankPermissionSource.class);
        Mockito.when(permissionSource.getRankPermissions(rank1)).thenReturn(permissions1);
        Mockito.when(permissionSource.getRankPermissions(rank2)).thenReturn(permissions2);
        this.services.register(this.plugin, RankPermissionSource.class, permissionSource, Priority.NORMAL);

        assertThat(this.reader.getPermissions(this.player1).getPermission("test"))
                .isEqualTo(TriState.FALSE);
        assertThat(this.reader.getPermissions(this.player1).getPermission("unknown"))
                .isEqualTo(TriState.UNDEFINED);
        assertThat(this.reader.getPermissions(this.player2).getPermission("test"))
                .isEqualTo(TriState.TRUE);
        assertThat(this.reader.getPermissions(this.player2).getPermission("unknown"))
                .isEqualTo(TriState.UNDEFINED);
        assertThat(this.reader.getPermissions(this.player3).getPermission("test"))
                .isEqualTo(TriState.UNDEFINED);
        assertThat(this.reader.getPermissions(this.player3).getPermission("unknown"))
                .isEqualTo(TriState.UNDEFINED);
    }
}
