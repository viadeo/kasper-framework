package com.viadeo.kasper.resilience;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.junit.Test;

import static org.junit.Assert.*;

public class HystrixGatewayUTest {

    @Test
    public void testRegisterMetrics_should_not_throws_exception() throws Exception {

        // nothing is initialized
        HystrixGateway.registerMetricPlugin();
        assertTrue(HystrixGateway.metricNotInitialized);

        // initialized a metric registry for kasper metric
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        HystrixGateway.registerMetricPlugin();
        assertFalse(HystrixGateway.metricNotInitialized);

        // try to register the metrics a second time, hystrix throws an IllegalStateException in this case
        HystrixGateway.registerMetricPlugin();
    }
}