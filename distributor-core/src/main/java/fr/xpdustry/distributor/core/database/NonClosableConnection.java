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
package fr.xpdustry.distributor.core.database;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

// This code is provided to you by LuckPerms, under the MIT license.
/**
 * A wrapper around a {@link Connection} which blocks usage of the default {@link #close()} method.
 */
public final class NonClosableConnection implements Connection {
    private final Connection delegate;

    public NonClosableConnection(final Connection delegate) {
        this.delegate = delegate;
    }

    public void close0() throws SQLException {
        this.delegate.close();
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public boolean isWrapperFor(final Class<?> inter) throws SQLException {
        return inter.isInstance(this.delegate) || this.delegate.isWrapperFor(inter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> inter) throws SQLException {
        if (inter.isInstance(this.delegate)) {
            return (T) this.delegate;
        }
        return this.delegate.unwrap(inter);
    }

    // Forward to the delegate connection
    @Override
    public Statement createStatement() throws SQLException {
        return this.delegate.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return this.delegate.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return this.delegate.prepareCall(sql);
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return this.delegate.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.delegate.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.delegate.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        this.delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.delegate.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.delegate.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.delegate.getMetaData();
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.delegate.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.delegate.isReadOnly();
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.delegate.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.delegate.getCatalog();
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.delegate.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.delegate.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.delegate.clearWarnings();
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.delegate.getTypeMap();
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this.delegate.setTypeMap(map);
    }

    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this.delegate.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.delegate.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.delegate.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return this.delegate.setSavepoint(name);
    }

    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.delegate.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.delegate.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(
            final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {
        return this.delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(
            final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {
        return this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(
            final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {
        return this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return this.delegate.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return this.delegate.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return this.delegate.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.delegate.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.delegate.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.delegate.createSQLXML();
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return this.delegate.isValid(timeout);
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this.delegate.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        this.delegate.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        return this.delegate.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.delegate.getClientInfo();
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return this.delegate.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return this.delegate.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        this.delegate.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.delegate.getSchema();
    }

    @Override
    public void abort(final Executor executor) throws SQLException {
        this.delegate.abort(executor);
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        this.delegate.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.delegate.getNetworkTimeout();
    }
}
