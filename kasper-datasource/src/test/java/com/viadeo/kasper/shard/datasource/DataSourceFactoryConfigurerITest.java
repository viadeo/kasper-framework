// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.datasource;

import com.viadeo.kasper.shard.Operation;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

// integration test
@Ignore
public class DataSourceFactoryConfigurerITest {

    public static String dsFile = "datasources.json";
    public static String dispatcherFile = "dispatcher.json";

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptiononNullFirstParameter() {
        // Given
        final DataSourceFactoryConfigurer builder = new DataSourceFactoryConfigurer(null,dispatcherFile);

        // When
        final DataSourceFactory dsf = builder.configureDatasourceFactory();

        // Then
        fail(); // should stop before
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptiononNullSecondParameter() {
        // Given
        final DataSourceFactoryConfigurer builder = new DataSourceFactoryConfigurer(dsFile,null);

        // When
        final DataSourceFactory dsf = builder.configureDatasourceFactory();

        // Then
        fail(); // should stop before
    }

    @Test
    public void shouldRetrieveReadDatasourceForMembers() throws IOException {
        // Given
        final DataSourceFactoryConfigurer builder = new DataSourceFactoryConfigurer(dsFile,dispatcherFile);
        final DataSourceFactory dsf = builder.configureDatasourceFactory();
        final Map<String, DataSource> dataSourceMap = dsf.getDataSourcesMap();

        // When
        final DataSource ds =  dsf.getDatasource("member", Operation.READ);

        // Then
        assertNotNull(ds);
        assertEquals(dataSourceMap.get("viadeo-balanced"), ds);
    }

    @Test
    public void shouldRetrieveWriteDatasourceForMembers() throws IOException {
        // Given
        final DataSourceFactoryConfigurer builder = new DataSourceFactoryConfigurer(dsFile,dispatcherFile);
        final DataSourceFactory dsf = builder.configureDatasourceFactory();
        final Map<String, DataSource> dataSourceMap = dsf.getDataSourcesMap();

        // When
        final DataSource ds =  dsf.getDatasource("member", Operation.WRITE);

        // Then
        assertNotNull(ds);
        assertEquals(dataSourceMap.get("viadeo"), ds);
    }

}
