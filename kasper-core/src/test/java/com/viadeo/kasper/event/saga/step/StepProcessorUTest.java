// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaIdReconciler;
import com.viadeo.kasper.event.saga.TestFixture;
import com.viadeo.kasper.event.saga.step.facet.FacetApplierRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.viadeo.kasper.event.saga.TestFixture.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StepProcessorUTest {


    private SagaIdReconciler idReconciler;

    @Before
    public void setUp() throws Exception {
        idReconciler = SagaIdReconciler.NONE;
    }

    @Test
    public void process_resolveThenCheck() {
        // Given
        Set<Step> givenSteps = Sets.newHashSet();
        StepChecker checker = mock(StepChecker.class);
        StepResolver resolver = mock(StepResolver.class);
        when(resolver.resolve(Saga.class, idReconciler)).thenReturn(givenSteps);
        StepProcessor processor = new StepProcessor(checker, resolver);

        // When
        Set<Step> steps = processor.process(Saga.class, idReconciler);

        // Then
        assertNotNull(steps);
        verify(resolver).resolve(Saga.class, idReconciler);
        verify(checker).check(Saga.class, givenSteps);
    }

    @Test
    public void process_withTestSaga_isOK() {
        // Given
        FacetApplierRegistry facetApplierRegistry = new FacetApplierRegistry();
        StepProcessor processor = new StepProcessor(
                new Steps.StartStepResolver(facetApplierRegistry),
                new Steps.EndStepResolver(facetApplierRegistry),
                new Steps.BasicStepResolver(facetApplierRegistry)
        );

        // When
        Set<Step> steps = processor.process(TestFixture.TestSagaB.class, idReconciler);

        // Then
        assertNotNull(steps);
        assertTrue(steps.contains(new Steps.StartStep(getMethod(TestSagaB.class, "start", StartEvent.class), "getId", idReconciler)));
        assertTrue(steps.contains(new Steps.EndStep(getMethod(TestSagaB.class, "end", EndEvent.class), "getId", idReconciler)));
        assertTrue(steps.contains(new Steps.BasicStep(getMethod(TestSagaB.class, "step", StepEvent.class), "getId", idReconciler)));
    }
}
