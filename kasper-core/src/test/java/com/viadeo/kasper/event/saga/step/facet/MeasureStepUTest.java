// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.core.resolvers.SagaResolver;
import com.viadeo.kasper.event.saga.TestFixture;
import com.viadeo.kasper.event.saga.step.Steps;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.event.saga.TestFixture.getMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MeasureStepUTest {

    @Before
    public void setUp() throws Exception {
        SagaResolver sagaResolver = new SagaResolver();
        sagaResolver.setDomainResolver(new DomainResolver());

        ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setSagaResolver(sagaResolver);

        KasperMetrics.setResolverFactory(resolverFactory);
    }

    @Test
    public void getMetricName_fromMeasurableStartStep_isOk() throws Exception {
        // Given
        MetricRegistry metricRegistry = new MetricRegistry();
        Steps.StartStep startStep = new Steps.StartStep(getMethod(TestFixture.TestSagaA.class, "handle", TestFixture.TestEvent.class), "getId");
        MeasureStep step = new MeasureStep(metricRegistry, startStep);

        // When
        String metricName = step.getMetricName();

        // Then
        assertNotNull(metricName);
        assertEquals("test.saga.testsagaa.start", metricName);
    }
}
