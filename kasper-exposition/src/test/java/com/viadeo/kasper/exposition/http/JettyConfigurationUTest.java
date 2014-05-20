// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JettyConfigurationUTest {

    @Test
    public void getAcceptors_auto() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("acceptorThreads", ConfigValueFactory.fromAnyRef("auto"))
        );
        assertEquals(JettyConfiguration.DEFAULT_ACCEPTORS, conf.getAcceptors());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAcceptors_with_bad_string_throws_exception() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("acceptorThreads", ConfigValueFactory.fromAnyRef("bad-string"))
        );
        conf.getAcceptors();
    }

    @Test
    public void getMaxBuffers_auto() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("maxBufferCount", ConfigValueFactory.fromAnyRef("auto"))
        );
        assertEquals(JettyConfiguration.DEFAULT_ACCEPTORS, conf.getMaxBuffers());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMaxBuffers_with_bad_string_throws_exception() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("maxBufferCount", ConfigValueFactory.fromAnyRef("bad-string"))
        );
        conf.getMaxBuffers();
    }

    @Test
    public void getPoolMinThreads_auto() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("minThreads", ConfigValueFactory.fromAnyRef("auto"))
        );
        assertEquals(JettyConfiguration.DEFAULT_MIN_THREADS, conf.getPoolMinThreads());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPoolMinThreads_with_bad_string_throws_exception() {
        final JettyConfiguration conf = new JettyConfiguration(ConfigFactory.empty()
                .withValue("minThreads", ConfigValueFactory.fromAnyRef("bad-string"))
        );
        conf.getPoolMinThreads();
    }

}
