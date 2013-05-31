package com.viadeo.kasper.db.jdbc;

import com.viadeo.kasper.db.Operation;
import com.viadeo.kasper.db.datasource.DataSourceFactory;
import com.viadeo.kasper.db.datasource.DataSourceFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;


public class ViadeoDataSource implements DataSource {
    
	private static Logger log = LoggerFactory.getLogger(ViadeoDataSource.class);
    
	DataSourceFactory dsResolver;
	Connection connection;
    PrintWriter logWriter;
    int loginTimeout = 0;
    

	public ViadeoDataSource(DataSourceFactoryBuilder builder) {
		dsResolver = builder.buildDatasourceFactory();
		try {
			connection = dsResolver.getDefaultDatasource().getConnection();
		} catch (SQLException e) {
			log.error("SQL Error", e);
		}
	}
	
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("not supported");
    }

    @Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		log.debug("Getconnection Interceptor : Get proxy");
		Connection connection = (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{Connection.class}, new ViadeoConnectionHandler(this));
        return connection;
	    
	}
	
	public Connection getDefaultConnection() {
		return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{Connection.class}, new UnClosableViadeoConnectionHandler(connection));
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
	    throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return dsResolver.getDefaultDatasource().getLogWriter();
	}

	public Connection getConnection(String tableName, Operation accessMode) throws SQLException {
		log.debug("Getconnection Interceptor with table name : " + tableName +"  and " +  accessMode);
		DataSource ds = dsResolver.getDatasource(tableName, accessMode);
		return ds.getConnection();
	}

}
