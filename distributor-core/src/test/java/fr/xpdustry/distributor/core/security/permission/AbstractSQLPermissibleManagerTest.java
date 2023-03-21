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

import fr.xpdustry.distributor.api.security.permission.Permissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractSQLPermissibleManagerTest<P extends Permissible> {

    private ConnectionFactory factory;
    private @TempDir Path tempDir;

    @BeforeEach
    void createFactory() {
        this.factory = new SQLiteConnectionFactory(
                "", this.tempDir.resolve("test.db"), this.getClass().getClassLoader());
        this.factory.start();
        Permissibles.createDatabase(this.factory);
    }

    @AfterEach
    void closeFactory() throws Exception {
        this.factory.close();
    }

    @Test
    void test_save() {
        final var manager = this.createManager(this.factory);
        final var permissible = this.createRandomPermissible();

        assertThat(manager.count()).isEqualTo(0);
        assertThat(manager.findAll()).isEmpty();

        manager.save(permissible);

        assertThat(manager.count()).isEqualTo(1);
        assertThat(manager.findAll()).singleElement().isNotSameAs(permissible).isEqualTo(permissible);
    }

    @Test
    void test_save_all() {
        final var manager = this.createManager(this.factory);
        final var permissible1 = this.createRandomPermissible();
        final var permissible2 = this.createRandomPermissible();

        manager.saveAll(List.of(permissible1, permissible2));

        assertThat(manager.count()).isEqualTo(2);
        assertThat(manager.findAll()).containsExactlyInAnyOrder(permissible1, permissible2);
    }

    @Test
    void test_modify() {
        final var manager = this.createManager(this.factory);
        final var permissible = this.createRandomPermissible();

        permissible.setPermission("test", true);
        manager.save(permissible);
        assertThat(manager.findAll()).singleElement().isEqualTo(permissible);

        permissible.setPermission("test", false);
        manager.save(permissible);
        assertThat(manager.findAll()).singleElement().isEqualTo(permissible);
    }

    @Test
    void test_find() {
        final var manager = this.createManager(this.factory);
        final var permissible = this.createRandomPermissible();

        assertThat(manager.findById(this.extractIdentifier(permissible))).isEmpty();
        manager.save(permissible);
        assertThat(manager.findById(this.extractIdentifier(permissible)))
                .isPresent()
                .get()
                .isNotSameAs(permissible)
                .isEqualTo(permissible);
    }

    @Test
    void test_exists() {
        final var manager = this.createManager(this.factory);
        final var permissible = this.createRandomPermissible();

        assertThat(manager.existsById(this.extractIdentifier(permissible))).isFalse();
        assertThat(manager.exists(permissible)).isFalse();

        manager.save(permissible);

        assertThat(manager.existsById(this.extractIdentifier(permissible))).isTrue();
        assertThat(manager.exists(permissible)).isTrue();
    }

    @Test
    void test_delete() {
        final var manager = this.createManager(this.factory);
        final var permissible1 = this.createRandomPermissible();
        final var permissible2 = this.createRandomPermissible();

        manager.save(permissible1);
        manager.save(permissible2);

        assertThat(manager.count()).isEqualTo(2);
        assertThat(manager.exists(permissible1)).isTrue();
        assertThat(manager.exists(permissible2)).isTrue();

        manager.delete(permissible1);
        assertThat(manager.count()).isEqualTo(1);
        assertThat(manager.exists(permissible1)).isFalse();
        assertThat(manager.exists(permissible2)).isTrue();

        manager.deleteById(this.extractIdentifier(permissible2));
        assertThat(manager.count()).isEqualTo(0);
        assertThat(manager.exists(permissible1)).isFalse();
        assertThat(manager.exists(permissible2)).isFalse();
    }

    @Test
    void test_delete_all() {
        final var manager = this.createManager(this.factory);
        final var permissible1 = this.createRandomPermissible();
        final var permissible2 = this.createRandomPermissible();

        manager.saveAll(List.of(permissible1, permissible2));
        assertThat(manager.count()).isEqualTo(2);

        manager.deleteAll(List.of(permissible1, permissible2));
        assertThat(manager.count()).isEqualTo(0);
    }

    protected abstract PermissibleManager<P> createManager(final ConnectionFactory factory);

    protected abstract P createRandomPermissible();

    protected abstract String extractIdentifier(final P permissible);
}
