// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step.facet;

import com.viadeo.kasper.core.component.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.saga.step.facet.SchedulingFacetApplier;
import com.viadeo.kasper.core.component.saga.step.facet.SchedulingStep;
import com.viadeo.kasper.core.component.saga.TestFixture;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.core.component.saga.step.Step;
import com.viadeo.kasper.core.component.saga.step.Steps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SchedulingFacetApplierUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private SchedulingFacetApplier facetApplier;

    @Before
    public void setUp() throws Exception {
        facetApplier = new SchedulingFacetApplier(mock(Scheduler.class));
    }

    @Test
    public void apply_on_method_without_schedule_annotation_return_initial_step() {
        // Given
        Step step = mock(Step.class);
        Method method = TestFixture.getMethod(TestFixture.TestSagaA.class, "notScheduledStep", TestFixture.TestEvent5.class);

        // When
        Step actualStep = facetApplier.apply(method, step);

        // Then
        assertNotNull(actualStep);
        assertEquals(step, actualStep);
    }

    @Test
    public void apply_on_method_with_schedule_annotation_return_a_scheduling_step() {
        // Given
        Method method = TestFixture.getMethod(TestFixture.TestSagaA.class, "scheduledStep", TestFixture.TestEvent6.class);
        Steps.BasicStep step = new Steps.BasicStep(method, "getId", mock(SagaIdReconciler.class));

        // When
        Step actualStep = facetApplier.apply(method, step);

        // Then
        assertNotNull(actualStep);
        assertNotEquals(step, actualStep);
        assertTrue(actualStep instanceof SchedulingStep);
    }

    @Test
    public void apply_on_method_with_schedule_and_cancel_annotation_return_throw_exception() {
        Step step = new Steps.BasicStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "scheduledAndCancelStep", TestFixture.TestEvent6.class), "getId", mock(SagaIdReconciler.class));
        Method method = TestFixture.getMethod(TestFixture.TestSagaA.class, "scheduledAndCancelStep", TestFixture.TestEvent6.class);

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should have one schedule annotation per step : " + TestFixture.TestSagaA.class.getName());

        // When
        facetApplier.apply(method, step);
    }
}
