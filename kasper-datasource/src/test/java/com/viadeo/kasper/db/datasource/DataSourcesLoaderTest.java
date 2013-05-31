package com.viadeo.kasper.db.datasource;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class DataSourcesLoaderTest {

    public static String configFile = "classpath:datasources.json";

    @Test
    public void shouldReturnListAfterReadingJsonConfiguration() {
        DataSourcesSettings result = null;
        try {
            result = DataSourcesLoader.read(configFile);
        } catch (IOException e) {
            fail("no setting file found");
        }
        assertNotNull(result);
    }
}
