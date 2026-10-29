// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.database;

import com.xpdustry.nucleus.testing.TestApplication;
import java.nio.file.Path;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PostgresDatabaseTest {

    @TempDir
    private static Path directory;

    private static TestApplication application;
    private static PostgresDatabase database;

    @BeforeAll
    static void setup() {
        application = TestApplication.start(directory, new DatabaseModule());
        database = application.resolve(PostgresDatabase.class);
    }

    @AfterAll
    static void teardown() {
        application.close();
    }

    @Test
    void nested_transactions_join_the_outer_one() {
        database.withTransaction(outer -> {
            assertThat(database.withTransaction(PostgresDatabaseTest::backendPid))
                    .isEqualTo(backendPid(outer));
            return Boolean.TRUE;
        });
    }

    @Test
    void a_failed_transaction_rolls_back_nested_calls_included() {
        assertThatThrownBy(() -> database.withTransaction(_ -> {
                    database.withTransaction(transaction -> transaction
                            .prepareStatement("""
                                    INSERT INTO "address_whitelist"("address", "reason") VALUES (?::inet, ?)
                                    """)
                            .bind("10.0.0.1")
                            .bind("test")
                            .executeSingleUpdate());
                    throw new IllegalStateException("boom");
                }))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        final int count = database.withTransaction(transaction -> transaction
                .prepareStatement("SELECT count(*) FROM \"address_whitelist\"")
                .executeSelect(result -> result.getInt(1))
                .getFirst());
        assertThat(count).isZero();
    }

    private static int backendPid(final PostgresDatabase.Transaction transaction) throws SQLException {
        return transaction
                .prepareStatement("SELECT pg_backend_pid()")
                .executeSelect(result -> result.getInt(1))
                .getFirst();
    }
}
