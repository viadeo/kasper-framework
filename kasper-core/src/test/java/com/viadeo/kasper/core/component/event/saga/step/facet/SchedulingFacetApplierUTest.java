// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.step.Scheduler;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
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
