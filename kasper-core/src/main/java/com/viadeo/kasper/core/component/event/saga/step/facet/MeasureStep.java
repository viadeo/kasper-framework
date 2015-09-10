// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.StepInvocationException;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.metrics.MetricNameStyle;

import static com.google.common.base.Preconditions.checkNotNull;

public class MeasureStep extends DecorateStep {

    private final MetricRegistry metricRegistry;
    private final String metricName;

    // ------------------------------------------------------------------------

    public MeasureStep(final MetricRegistry metricRegistry, final Step delegateStep) {
        super(delegateStep);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.metricName = KasperMetrics.name(
            MetricNameStyle.DOMAIN_TYPE_COMPONENT, delegateStep.getSagaClass(), delegateStep.getClass().getSimpleName().replace("Step", "").toLowerCase()
        );
    }

    @VisibleForTesting
    protected String getMetricName() {
        return metricName;
    }

    @Override
    public void invoke(final Saga saga, final Context context, final Event event) throws StepInvocationException {
        try {
            getDelegateStep().invoke(saga, context, event);
        } finally {
            metricRegistry.meter(metricName).mark();
        }
    }

    @Override
    protected String getAction() {
        return "Measure";
    }
}
