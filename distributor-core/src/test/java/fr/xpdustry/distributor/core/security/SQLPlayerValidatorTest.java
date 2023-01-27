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
package fr.xpdustry.distributor.core.security;

import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

public final class SQLPlayerValidatorTest {

    private static final MUUID PLAYER_1A = MUUID.of("1AAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");
    private static final MUUID PLAYER_1B = MUUID.of("1AAAAAAAAAAAAAAAAAAAAA==", "BAAAAAAAAAA=");
    private static final MUUID PLAYER_2A = MUUID.of("2AAAAAAAAAAAAAAAAAAAAA==", "AAAAAAAAAAA=");
    private static final MUUID PLAYER_2B = MUUID.of("2AAAAAAAAAAAAAAAAAAAAA==", "BAAAAAAAAAA=");

    private SQLiteConnectionFactory factory;
    private @TempDir Path dbDir;
    private SQLPlayerValidator validator;

    @BeforeEach
    void setup() {
        this.factory = new SQLiteConnectionFactory(
                "test_", this.dbDir.resolve("test.db"), this.getClass().getClassLoader());
        this.factory.start();
        this.validator = new SQLPlayerValidator(this.factory);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.factory.close();
    }

    @Test
    void test_validate() {
        assertThat(this.validator.isValid(PLAYER_1A)).isFalse();
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isFalse();
        this.validator.validate(PLAYER_1A);
        assertThat(this.validator.isValid(PLAYER_1A)).isTrue();
    }

    @Test
    void test_contains_validated() {
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isFalse();
        this.validator.validate(PLAYER_1A);
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isTrue();
    }

    @Test
    void test_contains_invalidated() {
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isFalse();
        this.validator.invalidate(PLAYER_1A);
        assertThat(this.validator.contains(PLAYER_1A.getUuid())).isTrue();
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
    }
}
