// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.event.listener.AxonEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaWrapper extends AxonEventListener<Event> implements EventListener<Event> {

    private final SagaExecutor executor;

    // ------------------------------------------------------------------------

    public SagaWrapper(final MetricRegistry metricRegistry, final SagaExecutor executor) {
        super(metricRegistry);
        this.executor = checkNotNull(executor);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final Context context, final Event event) {
        try {
            executor.execute(context, event);
        } catch (final Exception e) {
            return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }
        return EventResponse.success();
    }

    @Override
    public void rollback(Context context, Event event) { }

    @Override
    public String getName() {
        return executor.getSagaClass().getName();
    }

    @Override
    public Set<Class<?>> getEventClasses() {
        return executor.getEventClasses();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<Event> getInputClass() {
        return Event.class;
    }

    @Override
    public Class<?> getHandlerClass() {
        return executor.getSagaClass();
    }
}
