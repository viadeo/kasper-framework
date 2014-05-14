// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sun.jersey.api.core.ResourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JettyHttpServerUTest {

    @Mock
    JettyConfiguration configuration;

    @Mock
    HttpQueryExposer httpQueryExposer;

    @Mock
    HttpCommandExposer httpCommandExposer;

    @Mock
    HttpEventExposer httpEventExposer;

    @Mock
    ResourceConfig resourceConfig;

    @Test(expected = IllegalStateException.class)
    public void testStopThrowIllegalStateExceptionWhenNotStarted() throws Exception {
        JettyHttpServer server = new JettyHttpServer(
                configuration,
                httpQueryExposer,
                httpCommandExposer,
                httpEventExposer,
                resourceConfig,
                new HealthCheckRegistry(),
                new MetricRegistry()
        );

        server.stop();
    }
}
