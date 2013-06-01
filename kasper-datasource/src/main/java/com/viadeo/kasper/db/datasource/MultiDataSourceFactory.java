// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.datasource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.viadeo.kasper.db.Operation;
import com.viadeo.kasper.db.dispatcher.DispatcherSettings;
import com.viadeo.kasper.db.dispatcher.Dsn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

public class MultiDataSourceFactory implements DataSourceFactory {
  	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    // Map containing key: database alias, value: javax.sql.Datasource
    private Map<String, DataSource> dataSourceMap;

    // Map containing key: table name, value: Dsn
    private Map<String, Dsn> dsnMap = Maps.newHashMap();

    private DispatcherSettings dispatcherSettings;

    // ------------------------------------------------------------------------

    /**
     * Create a container for all the datasource and expose utilities
     * methods for retrieve a datasource by a table name
     * @param dataSourceMap
     * @param dispatcherSettings
     * @throws NullPointerException if any parameter is null
     */
    MultiDataSourceFactory(final Map<String, DataSource> dataSourceMap, final DispatcherSettings dispatcherSettings) {

        // throws NPE if any parameter is null
        this.dataSourceMap = Preconditions.checkNotNull(dataSourceMap);
        this.dispatcherSettings = Preconditions.checkNotNull(dispatcherSettings);

        // contruct map containing the tablenames and the dsn
        for (final Dsn dsn : dispatcherSettings.getDsns()) {
            dsnMap.put(dsn.getTableName().toLowerCase(), dsn);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public DataSource getBalancedDatasource() {
		String name = dispatcherSettings.getBalancedDsn().getRead();
        return dataSourceMap.get(name);
    }

    @Override
    public DataSource getDatasource(final String tableName, final Operation access) {

        if (null == tableName) {
            throw new IllegalArgumentException("tableName is mandatory");
        }

        if (null == access) {
            throw new IllegalArgumentException("access is mandatory");
        }

        String dsName = null;

        final Dsn dsn = findDsn(tableName);
        switch (access) {
            case READ:
            	// Use slave, not writable
                dsName = dsn.getRead();
                break;
            case WRITE:
            	// Use master, writable
                dsName = dsn.getWrite();
                break;
            default:
            	// By default, use master, writable
                dsName = dsn.getWrite();
        }

        final DataSource ds = dataSourceMap.get(dsName);
        if (null == ds) {
			LOGGER.error("Datasource {} not found in datasource configuration file", dsName);
        }

        return ds;
    }

    @Override
    public DataSource getNetworkDatasource() {
		String name = dispatcherSettings.getNetworkDsn().getRead();
        return dataSourceMap.get(name);
		
    }

    private Dsn findDsn(final String tableName) {
        Dsn result = dsnMap.get(tableName.toLowerCase());
        if (null == result) {
            result = dispatcherSettings.getDefaultDsn();
        }
        return result;
    }


	@Override
	public DataSource getDefaultDatasource() {
		final String name = dispatcherSettings.getDefaultDsn().getWrite();
        return dataSourceMap.get(name);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, DataSource> getDataSourcesMap() {
        return ImmutableMap.copyOf(dataSourceMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Dsn> getDsnsMap() {
        return ImmutableMap.copyOf(dsnMap);
    }

}
