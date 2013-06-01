// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
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
        // Given
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(null,dispatcherFile);

        // When
        final DataSourceFactory dsf = builder.buildDatasourceFactory();

        // Then
        fail(); // should stop before
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptiononNullSecondParameter() {
        // Given
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,null);

        // When
        final DataSourceFactory dsf = builder.buildDatasourceFactory();

        // Then
        fail(); // should stop before
    }

    @Test
    public void shouldRetrieveReadDatasourceForMembers() throws IOException {
        // Given
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,dispatcherFile);
        final DataSourceFactory dsf = builder.buildDatasourceFactory();
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
        final DataSourceFactoryBuilder builder = new DataSourceFactoryBuilder(dsFile,dispatcherFile);
        final DataSourceFactory dsf = builder.buildDatasourceFactory();
        final Map<String, DataSource> dataSourceMap = dsf.getDataSourcesMap();

        // When
        final DataSource ds =  dsf.getDatasource("member", Operation.WRITE);

        // Then
        assertNotNull(ds);
        assertEquals(dataSourceMap.get("viadeo"), ds);
    }

}
