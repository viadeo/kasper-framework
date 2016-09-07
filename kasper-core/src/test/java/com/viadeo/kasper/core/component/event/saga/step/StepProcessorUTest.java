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
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

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
        assertTrue(steps.contains(new Steps.StartStep(TestFixture.getMethod(TestFixture.TestSagaB.class, "start", TestFixture.StartEvent.class), "getId", idReconciler)));
        assertTrue(steps.contains(new Steps.EndStep(TestFixture.getMethod(TestFixture.TestSagaB.class, "end", TestFixture.EndEvent.class), "getId", idReconciler)));
        assertTrue(steps.contains(new Steps.BasicStep(TestFixture.getMethod(TestFixture.TestSagaB.class, "step", TestFixture.StepEvent.class), "getId", idReconciler)));
    }
}
