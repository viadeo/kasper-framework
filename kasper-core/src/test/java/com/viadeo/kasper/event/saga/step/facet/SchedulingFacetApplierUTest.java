// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.facet;

import com.viadeo.kasper.event.saga.TestFixture;
import com.viadeo.kasper.event.saga.step.Scheduler;
import com.viadeo.kasper.event.saga.step.Step;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SchedulingFacetApplierUTest {

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
        Step step = mock(Step.class);
        Method method = TestFixture.getMethod(TestFixture.TestSagaA.class, "scheduledStep", TestFixture.TestEvent6.class);

        // When
        Step actualStep = facetApplier.apply(method, step);

        // Then
        assertNotNull(actualStep);
        assertNotEquals(step, actualStep);
        assertTrue(actualStep instanceof SchedulingStep);
    }
}
