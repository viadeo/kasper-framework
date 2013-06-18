// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.datasource;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.shard.settings.*;
import com.viadeo.kasper.exception.KasperRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DataSourceFactoryConfigurer {
    private static final Logger log = LoggerFactory.getLogger(DataSourceFactoryConfigurer.class);

    private String datasourceFile;  // datasources configuration files in JSON Format
    private String dispatcherFile;  // dispatcher configuration files in JSON Format

    private static final String NOT_READY_STATE_MESSAGE = "Missing parameter for this builder !!!";

    private boolean ready = false;  // keep the state to not rebuild

    private DataSourcesSettings datasourceSettings;
    private DispatcherSettings dispatcherSettings;

    private DataSourceProvider dataSourceProvider;
    private MultiDataSourceFactory factory; // keep a reference for created factory

    // Map containing key: database alias, value: javax.sql.Datasource
    private Map<String, DataSource> dataSources = new LinkedHashMap<String, DataSource>();

    // ------------------------------------------------------------------------

    public DataSourceFactoryConfigurer() { }

    public DataSourceFactoryConfigurer(final String datasourceFile, final String dispatcherFile) {
        this.datasourceFile = datasourceFile;
        this.dispatcherFile = dispatcherFile;
    }

    public DataSourceFactoryConfigurer(final String datasourceFile, final String dispatcherFile, final DataSourceProvider dsProvider) {
        this.datasourceFile = datasourceFile;
        this.dispatcherFile = dispatcherFile;
        this.dataSourceProvider = dsProvider;
    }

    // ------------------------------------------------------------------------

    /**
     * Build all the datasources based on the settings files
     *
     * @throws IllegalArgumentException when missing parameter
     */
    protected void buildDatasources() {
        Preconditions.checkArgument(null != datasourceSettings, NOT_READY_STATE_MESSAGE);

        for (final Entry<String, DataSourceSetting> entry : datasourceSettings.entrySet()) {
            dataSources.put(entry.getKey(), dataSourceProvider.provide(entry.getValue()));
        }
    }

    /**
     * Configure the DataSourceFactory
     *
     * @return DataSourceFactory a datasource factory which construct datasources form settings files
     * @throws IllegalStateException when missing parameters
     */
    public DataSourceFactory configureDatasourceFactory() {
        if (!ready) {
            Preconditions.checkState(null != datasourceFile, NOT_READY_STATE_MESSAGE);
            Preconditions.checkState(null != dispatcherFile, NOT_READY_STATE_MESSAGE);

            if (null == dataSourceProvider) {
                dataSourceProvider = new DBCPDataSourceProvider();
            }

            try {
                this.datasourceSettings = DataSourcesLoader.read(datasourceFile);
                this.dispatcherSettings = DispatcherLoader.read(dispatcherFile);
            } catch (final IOException e) {
                final String errorMessage = "Error while reading datasource/dispatching configuration files";
                log.error(errorMessage, e);
                throw new KasperRuntimeException(errorMessage, e);
            }

            // instantiate all the datasources
            buildDatasources();
            factory = new MultiDataSourceFactory(dataSources, dispatcherSettings);
            ready = true;
        }

        return factory;
    }

    // ------------------------------------------------------------------------

    public void setDatasourceFile(final String datasourceFile) {
        this.datasourceFile = datasourceFile;
    }

    public void setDispatcherFile(final String dispatcherFile) {
        this.dispatcherFile = dispatcherFile;
    }

    /**
     * Build all the datasources based on the settings files
     * Package visible for tests
     *
     * @throws IllegalArgumentException when missing parameter
     */
    void setProvider(final DataSourceProvider builder) {
        this.dataSourceProvider = builder;
    }

}
