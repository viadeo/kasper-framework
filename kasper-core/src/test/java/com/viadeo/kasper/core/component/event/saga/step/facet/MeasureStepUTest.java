// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.core.resolvers.SagaResolver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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
        Steps.StartStep startStep = new Steps.StartStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle", TestFixture.TestEvent.class), "getId", mock(SagaIdReconciler.class));
        MeasureStep step = new MeasureStep(metricRegistry, startStep);

        // When
        String metricName = step.getMetricName();

        // Then
        assertNotNull(metricName);
        assertEquals("test.saga.testsagaa.start", metricName);
    }
}
