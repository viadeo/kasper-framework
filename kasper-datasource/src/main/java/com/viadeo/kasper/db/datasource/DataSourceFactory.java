// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.datasource;

import com.viadeo.kasper.db.Operation;
import com.viadeo.kasper.db.dispatcher.Dsn;

import javax.sql.DataSource;
import java.util.Map;

public interface DataSourceFactory {

    /**
     * Gets the balanced connection.
     *
     * @return the balanced connection (readonly / slave)
     */
    DataSource getBalancedDatasource();

    /**
     * Gets the connection.
     *
     * @param tableName the table name
     * @param access the access
     * @return the connection
     */
    DataSource getDatasource(String tableName, Operation access);

    /**
     * Gets the connection.
     * @return the default Datasource  : Master 1 (viaduc), writable
     */
    DataSource getDefaultDatasource();

    /**
     * Gets the network connection.
     * @return the network connection
     */
    DataSource getNetworkDatasource();

    /**
     * Return a map containing the database alias as key and the datasource as value
     * @return Map key : database alias, value : datasource
     */
    Map<String, DataSource> getDataSourcesMap();

    /**
     * Return a map containing the table name as key and the dsn as value
     * @return Map key: table name, value: Dsn
     */
    Map<String, Dsn> getDsnsMap();

}
