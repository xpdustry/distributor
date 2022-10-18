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
package fr.xpdustry.distributor.permission;

import fr.xpdustry.distributor.util.*;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

public final class SimplePermissionPersistenceManagerTest {

  private static final String PLAYER1 = "Anuke";
  private static final String GROUP1 = "group1";
  private static final String GROUP2 = "group2";

  @TempDir
  private Path tempDir;

  private SimplePermissionManager manager;

  @BeforeEach
  void setup() {
    manager = new SimplePermissionManager(tempDir);
  }

  // TODO Improve tests
  @Test
  void test_player_save() {
    final var players = manager.getPlayerPermissionManager();
    final var player = players.findOrCreateById(PLAYER1).join();
    player.setPermission("fr.xpdustry.test.a", Tristate.TRUE);
    player.setPermission("fr.xpdustry.test.b", Tristate.FALSE);

    // TODO Use assert4j
    Assertions.assertEquals(0, players.count().join());
    players.save(player).join();
    Assertions.assertEquals(1, players.count().join());
  }
}
