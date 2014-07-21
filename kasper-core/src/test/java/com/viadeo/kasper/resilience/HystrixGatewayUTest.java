package com.viadeo.kasper.resilience;

import com.codahale.metrics.MetricRegistry;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HystrixGatewayUTest {

    @Test
    public void testRegisterMetrics_should_not_throws_exception() throws Exception {

        // nothing is initialized
        HystrixGateway.registerMetricPlugin(null);
        assertTrue(HystrixGateway.metricNotInitialized);

        MetricRegistry metricRegistry = new MetricRegistry();

        // initialized a metric registry for kasper metric
        HystrixGateway.registerMetricPlugin(metricRegistry);
        assertFalse(HystrixGateway.metricNotInitialized);

        // try to register the metrics a second time, hystrix throws an IllegalStateException in this case
        HystrixGateway.registerMetricPlugin(metricRegistry);
    }
}