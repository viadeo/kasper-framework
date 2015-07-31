// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.core.metrics.MetricNames;
import org.axonframework.repository.ConflictingAggregateVersionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * This implementation of <code>Handler</code> allows to add metrics.
 * 
 * @param <INPUT> the input class handled by this <code>Handler</code>.
 * @param <RESPONSE> the response returned by this <code>Handler</code>.
 *
 * @see Handler
 */
public abstract class MeasuredHandler<RESPONSE extends KasperResponse, INPUT, HANDLER extends Handler<RESPONSE,INPUT>>
        implements Handler<RESPONSE, INPUT>
{

    protected final HANDLER handler;
    private final MetricRegistry metricRegistry;

    private MetricNames inputMetricNames;
    private MetricNames domainMetricNames;
    private MetricNames globalMetricNames;

    public MeasuredHandler(
            final MetricRegistry metricRegistry,
            final HANDLER handler,
            final Class<?> globalComponent
    ) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.handler = checkNotNull(handler);
        this.globalMetricNames = instantiateGlobalMetricNames(checkNotNull(globalComponent));
    }

    protected MetricNames instantiateGlobalMetricNames(Class<?> componentClass) {
        return MetricNames.of(componentClass);
    }

    protected MetricNames instantiateInputMetricNames() {
        return MetricNames.of(handler.getInputClass());
    }

    protected MetricNames instantiateDomainMetricNames() {
        return MetricNames.byDomainOf(handler.getInputClass());
    }

    private MetricNames getOrInstantiateInputMetricNames() {
        if (inputMetricNames == null) {
            inputMetricNames = instantiateInputMetricNames();
        }
        return inputMetricNames;
    }

    private MetricNames getOrInstantiateDomainMetricNames() {
        if (domainMetricNames == null) {
            domainMetricNames = instantiateDomainMetricNames();
        }
        return domainMetricNames;
    }

    @Override
    public RESPONSE handle(final Context context, final INPUT command) {
        final MetricNames inputMetricNames = getOrInstantiateInputMetricNames();
        final MetricNames domainMetricNames = getOrInstantiateDomainMetricNames();

        metricRegistry.meter(inputMetricNames.requests).mark();
        metricRegistry.meter(domainMetricNames.requests).mark();
        metricRegistry.meter(name(MetricNameStyle.CLIENT_TYPE, context, getInputClass(), "requests")).mark();
        metricRegistry.meter(globalMetricNames.requests).mark();

        final Timer.Context inputTimer = metricRegistry.timer(inputMetricNames.requestsTime).time();
        final Timer.Context domainTimer = metricRegistry.timer(domainMetricNames.requestsTime).time();
        final Timer.Context globalTimer = metricRegistry.timer(globalMetricNames.requestsTime).time();

        RESPONSE response;

        try {
            response = handler.handle(context, command);
        } catch (final ConflictingAggregateVersionException e) {
            response = error(new KasperReason(CoreReasonCode.CONFLICT, e.getMessage()));
        } catch (final RuntimeException e) {
            response = error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        } finally {
            inputTimer.stop();
            domainTimer.stop();
            globalTimer.stop();
        }

        switch (response.getStatus()) {
            case OK:
            case SUCCESS:
            case ACCEPTED:
            case REFUSED:
                // nothing
                break;

            case ERROR:
            case FAILURE:
                metricRegistry.meter(inputMetricNames.errors).mark();
                metricRegistry.meter(domainMetricNames.errors).mark();
                metricRegistry.meter(name(MetricNameStyle.CLIENT_TYPE, context, getInputClass(), "errors")).mark();
                metricRegistry.meter(globalMetricNames.errors).mark();
                break;
        }

        return response;
    }

    @Override
    public Class<INPUT> getInputClass() {
        return handler.getInputClass();
    }

    @Override
    public Class<HANDLER> getHandlerClass() {
        return (Class<HANDLER>) handler.getHandlerClass();
    }

    public abstract RESPONSE error(final KasperReason reason);
}
