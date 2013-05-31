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

    // Map containing key: database alias, value: javax.sql.Datasource
    private Map<String, DataSource> dataSourceMap;

    // Map containing key: table name, value: Dsn
    private Map<String, Dsn> dsnMap = Maps.newHashMap();

    private DispatcherSettings dispatcherSettings;

	Logger log= LoggerFactory.getLogger(DataSourceFactory.class);

    /**
     * Create a container for all the datasource and expose utilities
     * methods for retrieve a datasource by a table name
     * @param dataSourceMap
     * @param dispatcherSettings
     * @throws NullPointerException if any parameter is null
     */
    MultiDataSourceFactory(Map<String, DataSource> dataSourceMap, DispatcherSettings dispatcherSettings) {
        // throws NPE if any parameter is null
        this.dataSourceMap = Preconditions.checkNotNull(dataSourceMap);
        this.dispatcherSettings = Preconditions.checkNotNull(dispatcherSettings);
        // contruct map containing the tablenames and the dsn
        for (Dsn dsn : dispatcherSettings.getDsns()) {
            dsnMap.put(dsn.getTableName().toLowerCase(), dsn);
        }
    }


    @Override
    public DataSource getBalancedDatasource() {
		String name = dispatcherSettings.getBalancedDsn().getRead();
        return dataSourceMap.get(name);
    }

    @Override
    public DataSource getDatasource(String tableName, Operation access) {

        if (tableName == null) throw new IllegalArgumentException("tableName is mandatory");
        if (access == null) throw new IllegalArgumentException("access is mandatory");

        String dsName = null;
        Dsn dsn = findDsn(tableName);
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
        DataSource ds = dataSourceMap.get(dsName);
        if (ds == null) {
			log.error("Datasource {} not found in datasource configuration file", dsName);
        }
        return ds;
    }

    @Override
    public DataSource getNetworkDatasource() {
		String name = dispatcherSettings.getNetworkDsn().getRead();
        return dataSourceMap.get(name);
		
    }

    private Dsn findDsn(String tableName) {
        Dsn result = dsnMap.get(tableName.toLowerCase());
        if (result == null) {
            result = dispatcherSettings.getDefaultDsn();
        }
        return result;
    }


	@Override
	public DataSource getDefaultDatasource() {
		String name = dispatcherSettings.getDefaultDsn().getWrite();
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
