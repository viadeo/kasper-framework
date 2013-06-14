// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard;

import com.viadeo.kasper.shard.datasource.DataSourceFactory;
import com.viadeo.kasper.shard.datasource.DataSourceFactoryConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class ShardedDataSource implements javax.sql.DataSource {
	private static Logger LOGGER = LoggerFactory.getLogger(ShardedDataSource.class);
    
	DataSourceFactory dsResolver;
	java.sql.Connection connection;
    PrintWriter logWriter;
    int loginTimeout = 0;

    // ------------------------------------------------------------------------

	public ShardedDataSource(final DataSourceFactoryConfigurer builder) {
		dsResolver = builder.configureDatasourceFactory();
		try {
			connection = dsResolver.getDefaultDatasource().getConnection();
		} catch (SQLException e) {
			LOGGER.error("SQL Error", e);
		}
	}

    // ------------------------------------------------------------------------

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
        logWriter = out;
	}

   	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return dsResolver.getDefaultDatasource().getLogWriter();
	}

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("not supported");
    }

    // ------------------------------------------------------------------------

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
        loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

    // ------------------------------------------------------------------------

    @Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return this.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.isWrapperFor(iface);
	}

    // ------------------------------------------------------------------------

	@Override
	public Connection getConnection() throws SQLException {
		LOGGER.debug("Getconnection Interceptor : Get proxy");
		final Connection connection = (ShardedConnection) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{ShardedConnection.class}, new ShardedConnectionHandler(this));

        return connection;
	    
	}
	
	public Connection getDefaultConnection() {
		return (ShardedConnection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{ShardedConnection.class}, new UnClosableConnectionHandler(connection));
	}

	@Override
	public Connection getConnection(final String username, final String password)
			throws SQLException {
	    throw new UnsupportedOperationException("not implemented");
	}

	public Connection getConnection(final String tableName, final Operation accessMode) throws SQLException {
		LOGGER.debug("Getconnection Interceptor with table name : " + tableName + "  and " + accessMode);

		final DataSource ds = dsResolver.getDatasource(tableName, accessMode);
		return ds.getConnection();
	}

}
