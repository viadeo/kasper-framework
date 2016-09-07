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

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.step.Scheduler;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.StepInvocationException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FacetApplierRegistryUTest {

    @Test
    public void list_should_return_an_ordered_collection_according_their_weight() {
        // Given
        MeasuringFacetApplier measuringFacetApplier = new MeasuringFacetApplier(mock(MetricRegistry.class));
        SchedulingFacetApplier schedulingFacetApplier = new SchedulingFacetApplier(mock(Scheduler.class));
        FacetApplier fakeFacetApplierA = createFakeFacetApplier();


        FacetApplierRegistry registry = new FacetApplierRegistry();
        registry.register(measuringFacetApplier);
        registry.register(schedulingFacetApplier);
        registry.register(createFakeFacetApplier());
        registry.register(fakeFacetApplierA);

        // When
        List<FacetApplier> orderedFacetAppliers = registry.list();

        // Then
        assertNotNull(orderedFacetAppliers);
        assertTrue(orderedFacetAppliers.size() == 4);
        assertEquals(schedulingFacetApplier, orderedFacetAppliers.get(2));
        assertEquals(measuringFacetApplier, orderedFacetAppliers.get(3));
    }

    private FacetApplier createFakeFacetApplier() {
        return new FacetApplier() {
                @Override
                public Step apply(final Method method, final Step step) {
                    return new DecorateStep(step) {
                        @Override
                        public void invoke(Saga saga, Context context, Event event) throws StepInvocationException {
                            step.invoke(saga, context, event);
                        }

                        @Override
                        protected String getAction() {
                            return "FakeFacet";
                        }
                    };
                }

                @Override
                public int getPhase() {
                    return -1;
                }
            };
    }
}
