// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.step.Step;

import java.lang.reflect.Method;

public class MeasuringFacetApplier implements FacetApplier {

    private final MetricRegistry metricRegistry;

    public MeasuringFacetApplier(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Step apply(Method method, Step step) {
        final Class<? extends Step> stepClass;

        if (step instanceof DecorateStep) {
            stepClass = ((DecorateStep) step).getDelegateStep().getClass();
        } else {
            stepClass = step.getClass();
        }

        if (stepClass.isAnnotationPresent(XKasperSaga.Start.class) || stepClass.isAnnotationPresent(XKasperSaga.End.class)) {
            return new MeasureStep(metricRegistry, step);
        }

        return step;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
