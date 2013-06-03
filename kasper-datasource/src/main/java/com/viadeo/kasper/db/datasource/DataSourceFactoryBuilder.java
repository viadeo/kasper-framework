package com.viadeo.kasper.db.datasource;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.db.dispatcher.DispatcherLoader;
import com.viadeo.kasper.db.dispatcher.DispatcherSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DataSourceFactoryBuilder {

    private static final String NOT_READY_STATE_MESSAGE = "Missing parameter for this builder !!!";

    private static Logger log = LoggerFactory.getLogger(DataSourceFactoryBuilder.class);

    private boolean ready = false;  // keep the state to not rebuild

    private String datasourceFile;  // datasources configuration files in JSON Format
    private String dispatcherFile;  // dispatcher configuration files in JSON Format

    private DataSourcesSettings datasourceSettings;
    private DispatcherSettings dispatcherSettings;

    private DataSourceBuilder builder;
    private MultiDataSourceFactory factory; // keep a reference for created factory

    // Map containing key: database alias, value: javax.sql.Datasource
    private Map<String, DataSource> dataSources = new LinkedHashMap<String, DataSource>();

    public DataSourceFactoryBuilder() {
    }

    public DataSourceFactoryBuilder(String datasourceFile, String dispatcherFile) {
        this.datasourceFile = datasourceFile;
        this.dispatcherFile = dispatcherFile;
    }

    public DataSourceFactoryBuilder(String datasourceFile, String dispatcherFile, DataSourceBuilder dsBuilder) {
        this.datasourceFile = datasourceFile;
        this.dispatcherFile = dispatcherFile;
        builder = dsBuilder;
    }

    /**
     * Build all the datasources based on the settings files
     *
     * @throws IllegalArgumentException when missing parameter
     */
    protected void buildDatasources() {
        Preconditions.checkArgument(null != datasourceSettings, NOT_READY_STATE_MESSAGE);
        for (final Entry<String, DataSourceSetting> entry : datasourceSettings.entrySet()) {
            dataSources.put(entry.getKey(), builder.build(entry.getValue()));
        }
    }

    /**
     * Build the DataSourceFactory
     *
     * @return DataSourceFactory a datasource factory which construct datasources form settings files
     * @throws IllegalStateException when missing parameters
     */
    public DataSourceFactory buildDatasourceFactory() {
        if (!ready) {
            Preconditions.checkState(null != datasourceFile, NOT_READY_STATE_MESSAGE);
            Preconditions.checkState(null != dispatcherFile, NOT_READY_STATE_MESSAGE);
            if (builder == null) {
                builder = new DBCPDataSourceBuilder();
            }
            try {
                this.datasourceSettings = DataSourcesLoader.read(datasourceFile);
                this.dispatcherSettings = DispatcherLoader.read(dispatcherFile);
            } catch (IOException e) {
                log.error("Error during reading configuration files", e);
            }
            // instantiate all the datasources
            buildDatasources();
            factory = new MultiDataSourceFactory(dataSources, dispatcherSettings);
            ready = true;
        }
        return factory;
    }


    public void setDatasourceFile(String datasourceFile) {
        this.datasourceFile = datasourceFile;
    }

    public void setDispatcherFile(String dispatcherFile) {
        this.dispatcherFile = dispatcherFile;
    }

    /**
     * Build all the datasources based on the settings files
     * Package visible for tests
     *
     * @throws IllegalArgumentException when missing parameter
     */
    void setBuilder(DataSourceBuilder builder) {
        this.builder = builder;
    }

}
