package com.viadeo.kasper.db.datasource;


import com.viadeo.kasper.db.Operation;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

// integration test
@Ignore
public class DataSourceFactoryBuilderITest {

    public static String dsFile = "classpath:datasources.json";
    public static String dispatcherFile = "classpath:dispatcher.json";

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptiononNullFirstParameter() {
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(null,dispatcherFile);
        DataSourceFactory dsf = builder.buildDatasourceFactory();
        fail(); // should stop before
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptiononNullSecondParameter() {
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,null);
        DataSourceFactory dsf = builder.buildDatasourceFactory();
        fail(); // should stop before
    }

    @Test
    public void shouldRetrieveReadDatasourceForMembers() throws IOException {
        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,dispatcherFile);
        // load datasources
        DataSourceFactory dsf = builder.buildDatasourceFactory();
        Map<String, DataSource> dataSourceMap = dsf.getDataSourcesMap();
        DataSource ds =  dsf.getDatasource("member", Operation.READ);
        assertNotNull(ds);
        assertEquals(dataSourceMap.get("viadeo-balanced"), ds);
    }

    @Test
    public void shouldRetrieveWriteDatasourceForMembers() throws IOException {

        DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,dispatcherFile);
        // load datasources
        DataSourceFactory dsf = builder.buildDatasourceFactory();
        Map<String, DataSource> dataSourceMap = dsf.getDataSourcesMap();
        DataSource ds =  dsf.getDatasource("member", Operation.WRITE);
        assertNotNull(ds);
        assertEquals(dataSourceMap.get("viadeo"), ds);
    }


}
