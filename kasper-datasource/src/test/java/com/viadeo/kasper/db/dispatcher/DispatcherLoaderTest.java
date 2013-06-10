// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.dispatcher;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DispatcherLoaderTest {

    public static String configFile = "dispatcher.json";

    @Test
    public void shouldReadJsonConfiguration() {
        // Given
        DispatcherSettings result = null;

        // When
        try {
            result = DispatcherLoader.read(configFile);
        } catch (IOException e) {
            fail("no setting file found");
        }

        // Then
        assertNotNull(result);
    }

}
