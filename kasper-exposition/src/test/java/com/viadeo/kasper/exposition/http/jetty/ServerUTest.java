// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sun.jersey.api.core.ResourceConfig;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServerUTest {

    @Mock
    ServerConfiguration configuration;

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
        Server server = new Server(
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
