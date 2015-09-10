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
