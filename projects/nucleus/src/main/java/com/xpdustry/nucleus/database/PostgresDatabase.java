// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.intellij.lang.annotations.Language;

public interface PostgresDatabase {

    <R> R withTransaction(SQLFunction<Transaction, R> function);

    Connection newOrphanConnection() throws SQLException;

    interface Transaction {
        StatementBuilder prepareStatement(@Language("SQL") String statement) throws SQLException;
    }

    interface StatementValueBinder<B extends StatementValueBinder<B>> {

        B bind(String value) throws SQLException;

        B bind(int value) throws SQLException;

        B bind(long value) throws SQLException;

        B bind(boolean value) throws SQLException;

        B bind(float value) throws SQLException;

        B bind(double value) throws SQLException;

        B bind(byte[] value) throws SQLException;

        B bind(Instant value) throws SQLException;
    }

    interface StatementBuilder extends StatementValueBinder<StatementBuilderExecute>, StatementBuilderExecute {

        <T> StatementBuilderExecute batch(Iterable<T> values, SQLBiConsumer<StatementValueBinder<?>, T> consumer)
                throws SQLException;
    }

    interface StatementBuilderExecute extends StatementValueBinder<StatementBuilderExecute> {

        <T> List<T> executeSelect(SQLFunction<ResultSet, T> mapper) throws SQLException;

        <T> Optional<T> executeSingleSelect(SQLFunction<ResultSet, T> mapper) throws SQLException;

        int executeUpdate() throws SQLException;

        boolean executeSingleUpdate() throws SQLException;
    }
}
