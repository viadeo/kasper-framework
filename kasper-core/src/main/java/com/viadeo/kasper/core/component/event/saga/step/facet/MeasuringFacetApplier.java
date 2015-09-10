// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.step.Step;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

public class MeasuringFacetApplier implements FacetApplier {

    private final MetricRegistry metricRegistry;

    // ------------------------------------------------------------------------

    public MeasuringFacetApplier(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
    }

    // ------------------------------------------------------------------------

    @Override
    public Step apply(final Method method, final Step step) {
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
