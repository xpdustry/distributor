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
package fr.xpdustry.distributor.core.security;

import arc.Events;
import fr.xpdustry.distributor.api.Distributor;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent.Type;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import fr.xpdustry.distributor.core.event.SimpleEventBus;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public final class SQLPlayerValidatorTest {

    private static final MUUID PLAYER_1A = MUUID.of("1AAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");
    private static final MUUID PLAYER_1B = MUUID.of("1AAAAAAAAAAAAAAAAAAAAA==", "BAAAAAAAAAA=");
    private static final MUUID PLAYER_2A = MUUID.of("2AAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");
    private static final MUUID PLAYER_2B = MUUID.of("2AAAAAAAAAAAAAAAAAAAAA==", "BAAAAAAAAAA=");

    private Player player1;
    private Player player2;
    private final Map<Player, PlayerValidatorEvent.Type> statuses = new HashMap<>();

    private SQLiteConnectionFactory factory;
    private @TempDir Path dbDir;
    private SQLPlayerValidator validator;

    @BeforeEach
    void setup() {
        this.factory = new SQLiteConnectionFactory(
                "test_", this.dbDir.resolve("test.db"), this.getClass().getClassLoader());
        this.factory.start();
        this.validator = new SQLPlayerValidator(this.factory);

        Groups.init();
        Groups.player.add(this.player1 = this.mock(PLAYER_1A));
        Groups.player.add(this.player2 = this.mock(PLAYER_2A));

        final var distributor = Mockito.mock(Distributor.class);
        final var eventBus = new SimpleEventBus();
        Mockito.when(distributor.getEventBus()).thenReturn(eventBus);
        DistributorProvider.set(distributor);
        eventBus.subscribe(
                PlayerValidatorEvent.class,
                Mockito.mock(MindustryPlugin.class),
                event -> this.statuses.put(event.player(), event.type()));
    }

    @AfterEach
    void tearDown() throws Exception {
        this.factory.close();
        Groups.clear();
        Events.clear();
        DistributorProvider.clear();
    }

    @Test
    void test_muuid_hash_length() {
        assertThat(SQLPlayerValidator.hash(PLAYER_1A)).hasSize(32);
    }

    @Test
    void test_validate() {
        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isFalse();

        this.validator.validate(PLAYER_1A);

        assertThat(this.validator.isValid(PLAYER_1A)).isTrue();
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isTrue();

        assertThat(this.statuses).containsExactlyInAnyOrderEntriesOf(Map.of(this.player1, Type.VALIDATED));
    }

    @Test
    void test_invalidate() {
        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isFalse();

        this.validator.invalidate(PLAYER_1A);

        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isTrue();

        assertThat(this.statuses).containsExactlyInAnyOrderEntriesOf(Map.of(this.player1, Type.INVALIDATED));
    }

    @Test
    void test_invalidate_muuid() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.isValid(PLAYER_1A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_1B)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2B)).isTrue();

        this.validator.invalidate(PLAYER_1A);

        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.isValid(PLAYER_1B)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2B)).isTrue();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        this.player2, Type.VALIDATED,
                        this.player1, Type.INVALIDATED));
    }

    @Test
    void test_invalidate_uuid() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.isValid(PLAYER_1A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_1B)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2B)).isTrue();

        this.validator.invalidate(PLAYER_1A.getUuid());

        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.isValid(PLAYER_1B)).isFalse();
        assertThat(this.validator.isValid(PLAYER_2A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2B)).isTrue();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        this.player2, Type.VALIDATED,
                        this.player1, Type.INVALIDATED));
    }

    @Test
    void test_invalidate_all() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.isValid(PLAYER_1A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_1B)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2A)).isTrue();
        assertThat(this.validator.isValid(PLAYER_2B)).isTrue();

        this.validator.invalidateAll();

        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.isValid(PLAYER_1B)).isFalse();
        assertThat(this.validator.isValid(PLAYER_2A)).isFalse();
        assertThat(this.validator.isValid(PLAYER_2B)).isFalse();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(
                        Map.of(this.player1, Type.INVALIDATED, this.player2, Type.INVALIDATED));
    }

    @Test
    void test_remove_muuid() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.contains(PLAYER_1A)).isTrue();
        assertThat(this.validator.contains(PLAYER_1B)).isTrue();
        assertThat(this.validator.contains(PLAYER_2A)).isTrue();
        assertThat(this.validator.contains(PLAYER_2B)).isTrue();

        this.validator.remove(PLAYER_1A);

        assertThat(this.validator.contains(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1B)).isTrue();
        assertThat(this.validator.contains(PLAYER_2A)).isTrue();
        assertThat(this.validator.contains(PLAYER_2B)).isTrue();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        this.player2, Type.VALIDATED,
                        this.player1, Type.REMOVED));
    }

    @Test
    void test_remove_uuid() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.contains(PLAYER_1A)).isTrue();
        assertThat(this.validator.contains(PLAYER_1B)).isTrue();
        assertThat(this.validator.contains(PLAYER_2A)).isTrue();
        assertThat(this.validator.contains(PLAYER_2B)).isTrue();

        this.validator.remove(PLAYER_1A.getUuid());

        assertThat(this.validator.contains(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1B)).isFalse();
        assertThat(this.validator.contains(PLAYER_2A)).isTrue();
        assertThat(this.validator.contains(PLAYER_2B)).isTrue();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        this.player2, Type.VALIDATED,
                        this.player1, Type.REMOVED));
    }

    @Test
    void test_remove_all() {
        this.validator.validate(PLAYER_1A);
        this.validator.validate(PLAYER_1B);
        this.validator.validate(PLAYER_2A);
        this.validator.validate(PLAYER_2B);

        assertThat(this.validator.contains(PLAYER_1A)).isTrue();
        assertThat(this.validator.contains(PLAYER_1B)).isTrue();
        assertThat(this.validator.contains(PLAYER_2A)).isTrue();
        assertThat(this.validator.contains(PLAYER_2B)).isTrue();

        this.validator.removeAll();

        assertThat(this.validator.contains(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1B)).isFalse();
        assertThat(this.validator.contains(PLAYER_2A)).isFalse();
        assertThat(this.validator.contains(PLAYER_2B)).isFalse();

        assertThat(this.statuses)
                .containsExactlyInAnyOrderEntriesOf(Map.of(this.player1, Type.REMOVED, this.player2, Type.REMOVED));
    }

    private Player mock(final MUUID muuid) {
        final var player = Mockito.mock(Player.class);
        Mockito.when(player.uuid()).thenReturn(muuid.getUuid());
        Mockito.when(player.usid()).thenReturn(muuid.getUsid());
        return player;
    }
}
