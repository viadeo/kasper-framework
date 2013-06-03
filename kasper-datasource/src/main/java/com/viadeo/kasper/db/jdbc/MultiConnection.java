package com.viadeo.kasper.db.jdbc;

import com.viadeo.kasper.db.Operation;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Incubation
 */
public class MultiConnection implements Connection {

	Connection connection;
	MultiDataSource dataSource;
	
	public MultiConnection(MultiDataSource ds, Connection c) {
		this.connection = c;
		this.dataSource = ds;
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public Statement createStatement() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public void commit() throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public void rollback() throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public void close() throws SQLException {
		connection.close();
	}

	@Override
	public boolean isClosed() throws SQLException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public boolean isReadOnly() throws SQLException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public String getCatalog() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		connection.close();
		connection = dataSource.getConnection("member", Operation.WRITE);
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public int getHoldability() throws SQLException {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// Auto-generated method stub

	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// Auto-generated method stub

	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// Auto-generated method stub

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// Auto-generated method stub
		return null;
	}

    @Override
    public void setSchema(String schema) throws SQLException {
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

}
