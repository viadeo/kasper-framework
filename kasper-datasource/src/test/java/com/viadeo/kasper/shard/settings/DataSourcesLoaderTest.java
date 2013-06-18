// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.settings;

import com.viadeo.kasper.shard.settings.DataSourcesLoader;
import com.viadeo.kasper.shard.settings.DataSourcesSettings;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DataSourcesLoaderTest {

    public static String configFile = "datasources.json";

    @Test
    public void shouldReturnListAfterReadingJsonConfiguration() {

        // Given
        DataSourcesSettings result = null;

        // When
        try {
            result = DataSourcesLoader.read(configFile);
        } catch (final IOException e) {
            fail("no setting file found");
        }

        // Then
        assertNotNull(result);
    }
}
