// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.resilience;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HystrixGatewayUTest {

    @Test
    public void testRegisterMetrics_should_register_only_once_and_without_exception() throws Exception {

        // Given
        // reset flag
        HystrixGateway.metricNotInitialized = true;
        final MetricRegistry metricRegistry = new MetricRegistry();
        final HystrixPlugins hystrixPlugins = mock(HystrixPlugins.class);

        // When
        // initialized a metric registry for kasper metric
        HystrixGateway.registerMetricPlugin(metricRegistry, hystrixPlugins);
        // try to register the metrics a second time, hystrix throws an IllegalStateException in this case
        HystrixGateway.registerMetricPlugin(metricRegistry, hystrixPlugins);

        // Then
        verify(hystrixPlugins, atMost(1)).registerMetricsPublisher(any(HystrixMetricsPublisher.class));
    }

}
