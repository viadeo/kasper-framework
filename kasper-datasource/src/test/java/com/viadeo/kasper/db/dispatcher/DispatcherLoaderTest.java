package com.viadeo.kasper.db.dispatcher;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class DispatcherLoaderTest {

    public static String configFile = "classpath:dispatcher.json";

    @Test
    public void shouldReadJsonConfiguration() {
        DispatcherSettings result = null;
        try {
            result = DispatcherLoader.read(configFile);
        } catch (IOException e) {
            fail("no setting file found");
        }
        assertNotNull(result);
    }
}
