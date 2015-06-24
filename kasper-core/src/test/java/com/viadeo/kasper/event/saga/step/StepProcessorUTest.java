// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.TestFixture;
import org.junit.Test;

import java.util.Set;

import static com.viadeo.kasper.event.saga.TestFixture.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StepProcessorUTest {


    @Test
    public void process_resolveThenCheck() {
        // Given
        Set<Step> givenSteps = Sets.newHashSet();
        StepChecker checker = mock(StepChecker.class);
        StepResolver resolver = mock(StepResolver.class);
        when(resolver.resolve(Saga.class)).thenReturn(givenSteps);
        StepProcessor processor = new StepProcessor(checker, resolver);

        // When
        Set<Step> steps = processor.process(Saga.class);

        // Then
        assertNotNull(steps);
        verify(resolver).resolve(Saga.class);
        verify(checker).check(Saga.class, givenSteps);
    }

    @Test
    public void process_withTestSaga_isOK() {
        // Given
        MetricRegistry metricRegistry = new MetricRegistry();
        FacetApplierRegistry facetApplierRegistry = new FacetApplierRegistry();
        StepProcessor processor = new StepProcessor(
                new Steps.StartStepResolver(metricRegistry, facetApplierRegistry),
                new Steps.EndStepResolver(metricRegistry, facetApplierRegistry),
                new Steps.BasicStepResolver(facetApplierRegistry)
        );

        // When
        Set<Step> steps = processor.process(TestFixture.TestSagaA.class);

        // Then
        assertNotNull(steps);
        assertTrue(steps.contains(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId")));
        assertTrue(steps.contains(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId")));
        assertTrue(steps.contains(new Steps.BasicStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId")));
    }
}
