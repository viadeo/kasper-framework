package com.viadeo.kasper.resilience;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HystrixGatewayUTest {


    @Test
    public void testRegisterMetrics_should_register_only_once_and_without_exception() throws Exception {

        // reset flag
        HystrixGateway.metricNotInitialized = true;
        MetricRegistry metricRegistry = new MetricRegistry();
        HystrixPlugins hystrixPlugins = spy(HystrixPlugins.getInstance());

        // initialized a metric registry for kasper metric
        HystrixGateway.registerMetricPlugin(metricRegistry, hystrixPlugins);
        // try to register the metrics a second time, hystrix throws an IllegalStateException in this case
        HystrixGateway.registerMetricPlugin(metricRegistry, hystrixPlugins);

        verify(hystrixPlugins, atMost(1)).registerMetricsPublisher(any(HystrixMetricsPublisher.class));

    }
}